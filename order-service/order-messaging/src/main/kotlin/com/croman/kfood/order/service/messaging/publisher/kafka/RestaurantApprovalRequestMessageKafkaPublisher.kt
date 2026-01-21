package com.croman.kfood.order.service.messaging.publisher.kafka

import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.croman.kfood.kafka.producer.KafkaMessageHelper
import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.order.service.domain.config.OrderServiceConfigData
import com.croman.kfood.order.service.domain.exception.OrderDomainException
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.croman.kfood.order.service.domain.ports.output.message.publisher.approval.RestaurantApprovalRequestMessagePublisher
import com.croman.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croman.kfood.outbox.OutboxStatus
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.function.BiConsumer

@Component
class RestaurantApprovalRequestMessageKafkaPublisher(
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalRequestAvroModel>,
    private val messagingDataMapper: OrderMessagingDataMapper,
    private val configData: OrderServiceConfigData,
    private val objectMapper: ObjectMapper
): RestaurantApprovalRequestMessagePublisher {
    private val logger = KotlinLogging.logger {}

    override fun publish(
        message: OrderApprovalOutboxMessage,
        outboxCallback: BiConsumer<OrderApprovalOutboxMessage, OutboxStatus>
    ) {
        val payload =
            runCatching { objectMapper.readValue(message.payload, OrderApprovalEventPayload::class.java) }
                .getOrElse { throw OrderDomainException("There was an error deserializing ${message.payload}", it) }
                ?: error("There was an error deserializing ${message.payload}")

        val sagaId = message.sagaId.toString()
        logger.info { "Received OrderApprovalOutboxMessage with saga $sagaId for order ${payload.orderId}" }

        val avroModel = with(messagingDataMapper) { payload.toAvroModel(sagaId) }
        try {
            kafkaProducer.send(
                topicName = configData.restaurantApprovalRequestTopicName,
                key = sagaId, // this guarantees that all the messages for this sagaId, will be ordered and in the same partition
                message = avroModel,
                callback = KafkaMessageHelper.kafkaCallback(
                    topic = configData.restaurantApprovalRequestTopicName,
                    orderId = payload.orderId,
                    avroMessage = avroModel,
                    outboxCallback = outboxCallback,
                    outboxMessage = message
                )
            )
        } catch (e: Exception) {
            logger.error(e) { "Error sending event payload to kafka ${message.payload}" }
            outboxCallback.accept(message, OutboxStatus.FAILED)
        }

        logger.info { "OrderPaymentEventPayload sent to Kafka queue for order ${payload.orderId} and saga $sagaId" }
    }
}