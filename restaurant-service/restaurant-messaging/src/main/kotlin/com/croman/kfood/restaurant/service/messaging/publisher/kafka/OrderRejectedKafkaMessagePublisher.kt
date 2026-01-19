package com.croman.kfood.restaurant.service.messaging.publisher.kafka

import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.croman.kfood.kafka.producer.KafkaMessageHelper
import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.restaurant.service.domain.config.RestaurantServiceConfigData
import com.croman.kfood.restaurant.service.domain.event.OrderApprovalEvent
import com.croman.kfood.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher
import com.croman.kfood.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class OrderRejectedKafkaMessagePublisher(
    private val dataMapper: RestaurantMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalResponseAvroModel>,
    private val configData: RestaurantServiceConfigData,
): OrderRejectedMessagePublisher {

    private val logger = KotlinLogging.logger {}

    override fun publish(event: OrderApprovalEvent.Rejected) {

        val orderId = event.orderApproval.orderId.value.toString()
        logger.info { "Received order rejected event for order $orderId" }
        val avroModel = with(dataMapper) { event.toAvroModel() }
        try {
            kafkaProducer.send(
                topicName = configData.restaurantApprovalResponseTopicName,
                key = orderId,
                message = avroModel,
                callback = KafkaMessageHelper.kafkaCallback(
                    topic = configData.restaurantApprovalResponseTopicName,
                    orderId = orderId,
                    avroMessage = avroModel
                )
            )
            logger.info { "Successfully published order rejected event for order $orderId" }
        } catch (e: Exception) {
            logger.error { "Failed to publish order rejected event for order $orderId: ${e.message}" }
        }

    }
}