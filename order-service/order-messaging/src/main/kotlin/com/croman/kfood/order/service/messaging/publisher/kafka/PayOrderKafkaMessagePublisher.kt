package com.croman.kfood.order.service.messaging.publisher.kafka

import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croman.kfood.order.service.messaging.publisher.kafka.OrderKafkaMessageHelper.Companion.kafkaCallback
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.croman.kfood.order.service.domain.config.OrderServiceConfigData
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.ports.output.message.publisher.approval.OrderPaidRestaurantRequestMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class PayOrderKafkaMessagePublisher(
    val mapper: OrderMessagingDataMapper,
    val configData: OrderServiceConfigData,
    val kafkaProducer: KafkaProducer<String, RestaurantApprovalRequestAvroModel>,
): OrderPaidRestaurantRequestMessagePublisher {

    private val logger = KotlinLogging.logger {}

    override fun publish(event: OrderEvent.Paid) {
        logger.info { "Received order-paid-event for order: ${event.order.id.value}" }
        val orderId = event.order.id.value.toString()
        val restaurantApprovalRequest = with(mapper) {
            event.toAvroModel()
        }
        try {
            kafkaProducer.send(
                topicName = configData.restaurantApprovalRequestTopicName,
                key = orderId,
                message = restaurantApprovalRequest,
                callback = kafkaCallback(
                    topic = configData.restaurantApprovalRequestTopicName,
                    orderId = restaurantApprovalRequest.orderId,
                    message = restaurantApprovalRequest
                )
            )
            logger.info { "Successfully published RestaurantApprovalRequestAvroModel for order: ${event.order.id.value}" }
        } catch (ex: Exception) {
            logger.error(ex) { "Error while sending RestaurantApprovalRequestAvroModel for order: ${event.order.id.value}" }
        }
    }
}