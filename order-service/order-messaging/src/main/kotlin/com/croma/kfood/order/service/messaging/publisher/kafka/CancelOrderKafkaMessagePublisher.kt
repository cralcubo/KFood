package com.croma.kfood.order.service.messaging.publisher.kafka

import com.croma.kfood.kafka.producer.service.KafkaProducer
import com.croma.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croma.kfood.order.service.messaging.publisher.kafka.OrderKafkaMessageHelper.Companion.kafkaCallback
import com.croman.kfood.kafka.order.avro.model.PaymentRequestAvroModel
import com.croman.kfood.order.service.domain.config.OrderServiceConfigData
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CancelOrderKafkaMessagePublisher(
    val mapper: OrderMessagingDataMapper,
    val configData: OrderServiceConfigData,
    val kafkaProducer: KafkaProducer<String, PaymentRequestAvroModel>,
): OrderCancelledPaymentRequestMessagePublisher {

    private val logger = KotlinLogging.logger {}

    override fun publish(event: OrderEvent.Cancelled) {
        logger.info { "Received order-cancelled-event for order: ${event.order.id.value}" }
        val paymentRequest = with(mapper) {
            event.toAvroModel()
        }
        try {
            kafkaProducer.send(
                topicName = configData.paymentRequestTopic,
                key = event.order.id.value.toString(),
                message = paymentRequest,
                callback = kafkaCallback(
                    topic = configData.paymentRequestTopic,
                    orderId = paymentRequest.orderId,
                    message = paymentRequest
                )
            )
            logger.info { "Successfully published PaymentRequestAvroModel for order: ${event.order.id.value}" }
        } catch (ex: Exception) {
            logger.error(ex) { "Error while sending PaymentRequestAvroModel for order: ${event.order.id.value}" }
        }
    }
}