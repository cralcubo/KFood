package com.croman.kfood.restaurant.service.messaging.publisher.kafka

import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.croman.kfood.kafka.producer.KafkaMessageHelper
import com.croman.kfood.kafka.producer.KafkaMessageHelper.Companion.kafkaCallback
import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.domain.config.RestaurantServiceConfigData
import com.croman.kfood.restaurant.service.domain.exception.RestaurantApplicationServiceException
import com.croman.kfood.restaurant.service.domain.mapper.RestaurantDataMapper
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderEventPayload
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderOutboxMessage
import com.croman.kfood.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher
import com.croman.kfood.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.logging.log4j.util.BiConsumer
import org.springframework.stereotype.Component

@Component
class RestaurantApprovalEventKafkaPublisher(
    private val dataMapper: RestaurantMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalResponseAvroModel>,
    private val configData: RestaurantServiceConfigData,
    private val objectMapper: ObjectMapper
) : RestaurantApprovalResponseMessagePublisher {
    private val logger = KotlinLogging.logger {}

    override fun publish(
        message: OrderOutboxMessage,
        outboxCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>
    ) {
        val payload: OrderEventPayload = runCatching {
            objectMapper.readValue(message.payload, OrderEventPayload::class.java)
        }.getOrElse { throw RestaurantApplicationServiceException("Could not deserialize ${message.payload}", it) }
        val sagaId = message.sagaId.toString()
        logger.info { "Publishing OrderOutboxMessage to kafka with sagaId $sagaId for order ${payload.orderId}" }

        val avroMessage = with(dataMapper) { payload.toAvroModel(sagaId) }
        try {
            kafkaProducer.send(
                topicName = configData.restaurantApprovalResponseTopicName,
                key = sagaId,
                message = avroMessage,
                callback = kafkaCallback(
                    topic = configData.restaurantApprovalResponseTopicName,
                    orderId = payload.orderId,
                    avroMessage = avroMessage,
                    outboxMessage = message,
                    outboxCallback = outboxCallback
                )
            )
            logger.info { "Successfully sent RestaurantApprovalResponseAvroModel for order ${payload.orderId} and sagaId $sagaId" }
        }catch (e: Exception) {
            logger.error(e) { "There was an error while sending the RestaurantApprovalResponseAvroModel for order ${payload.orderId} and sagaId $sagaId" }
            outboxCallback.accept(message, OutboxStatus.FAILED)
        }
    }
}