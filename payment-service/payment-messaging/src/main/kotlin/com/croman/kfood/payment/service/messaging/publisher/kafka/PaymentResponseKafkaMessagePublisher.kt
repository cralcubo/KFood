package com.croman.kfood.payment.service.messaging.publisher.kafka

import com.croman.kfood.kafka.order.avro.model.PaymentResponseAvroModel
import com.croman.kfood.kafka.producer.KafkaMessageHelper
import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.domain.config.PaymentServiceConfigData
import com.croman.kfood.payment.service.domain.exception.PaymentDomainException
import com.croman.kfood.payment.service.domain.outbox.model.OrderEventPayload
import com.croman.kfood.payment.service.domain.outbox.model.OrderOutboxMessage
import com.croman.kfood.payment.service.domain.port.output.message.publisher.PaymentResponseMessagePublisher
import com.croman.kfood.payment.service.messaging.mapper.PaymentMessagingDataMapper
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.logging.log4j.util.BiConsumer
import org.springframework.stereotype.Component
import kotlin.jvm.java

@Component
class PaymentResponseKafkaMessagePublisher(
    private val kafkaProducer: KafkaProducer<String, PaymentResponseAvroModel>,
    private val messagingDataMapper: PaymentMessagingDataMapper,
    private val configData: PaymentServiceConfigData,
    private val objectMapper: ObjectMapper
): PaymentResponseMessagePublisher {
    private val logger = KotlinLogging.logger {}

    override fun publish(
        message: OrderOutboxMessage,
        updateMessageCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>
    ) {
        val payload =
            runCatching { objectMapper.readValue(message.payload, OrderEventPayload::class.java) }
                .getOrElse { throw PaymentDomainException("There was an error deserializing ${message.payload}", it) }
                ?: error("There was an error deserializing ${message.payload}")

        val sagaId = message.sagaId.toString()
        logger.info { "Received OrderOutboxMessage with saga $sagaId for order ${payload.orderId}" }

        val avroModel = with(messagingDataMapper) { payload.toAvroModel(sagaId) }
        try {
            kafkaProducer.send(
                topicName = configData.paymentResponseTopicName,
                key = sagaId, // this guarantees that all the messages for this sagaId, will be ordered and in the same partition
                message = avroModel,
                callback = KafkaMessageHelper.kafkaCallback(
                    topic = configData.paymentResponseTopicName,
                    orderId = payload.orderId,
                    avroMessage = avroModel,
                    outboxCallback = updateMessageCallback,
                    outboxMessage = message
                )
            )
        } catch (e: Exception) {
            logger.error(e) { "Error sending event payload to kafka ${message.payload}" }
            updateMessageCallback.accept(message, OutboxStatus.FAILED)
        }

        logger.info { "OrderOutboxMessage sent to Kafka queue for order ${payload.orderId} and saga $sagaId" }
    }
}