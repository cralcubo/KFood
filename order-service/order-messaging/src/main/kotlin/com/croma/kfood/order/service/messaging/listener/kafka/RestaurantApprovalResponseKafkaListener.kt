package com.croma.kfood.order.service.messaging.listener.kafka

import com.croma.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croman.kfood.kafka.consumer.KafkaConsumer
import com.croman.kfood.kafka.order.avro.model.OrderApprovalStatus
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.croman.kfood.order.service.domain.ports.input.message.listener.approval.RestaurantApprovalResponseMessageListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload

class RestaurantApprovalResponseKafkaListener(
    private val dataMapper: OrderMessagingDataMapper,
    private val messageListener: RestaurantApprovalResponseMessageListener
): KafkaConsumer<RestaurantApprovalResponseAvroModel> {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(
        id= $$"${kafka-consumer-config.restaurant-approval-consumer-group-id}",
        topics = [$$"${order-service.restaurant-approval-response- topic-name}"]
    )
    override fun receive(
        @Payload messages: List<RestaurantApprovalResponseAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        logger.info { "${messages.size} number of restaurant approval responses received with keys: $keys, partitions: $partitions, offsets: $offsets" }
        messages.forEach {
            when(it.orderApprovalStatus) {
                OrderApprovalStatus.APPROVED -> {
                    logger.info { "Processing order approved for ${it.orderId}" }
                    with(dataMapper) {
                        messageListener.orderApproved(it.toResponse())
                    }
                }
                OrderApprovalStatus.REJECTED -> {
                    logger.info { "Processing order rejected  for ${it.orderId} with failure message: ${it.failureMessages}" }
                    with(dataMapper) {
                        messageListener.orderRejected(it.toResponse())
                    }
                }
            }

        }
    }
}