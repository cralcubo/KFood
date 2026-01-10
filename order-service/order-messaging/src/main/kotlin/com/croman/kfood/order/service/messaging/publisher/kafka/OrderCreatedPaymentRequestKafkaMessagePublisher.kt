package com.croman.kfood.order.service.messaging.publisher.kafka

import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croman.kfood.kafka.producer.KafkaMessageHelper.Companion.kafkaCallback
import com.croman.kfood.kafka.order.avro.model.PaymentRequestAvroModel
import com.croman.kfood.order.service.domain.config.OrderServiceConfigData
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class OrderCreatedPaymentRequestKafkaMessagePublisher(
    val mapper: OrderMessagingDataMapper,
    val configData: OrderServiceConfigData,
    val kafkaProducer: KafkaProducer<String, PaymentRequestAvroModel>, // PaymentRequestAvroModel => payment-request-topic
) : OrderCreatedPaymentRequestMessagePublisher {
    private val logger = KotlinLogging.logger {}

    /**
     * When an order created event is published, that event goes to
     * the topic: `payment-request-topic` with the key of the message being the
     * pending order ID.
     *
     * A consumer will process this event, in this case the PaymentService
     */
    override fun publish(event: OrderEvent.Created) {
        logger.info { "Received order-created-event for order: ${event.order.id.value}" }
        val paymentRequest = with(mapper) {
            event.toAvroModel()
        }
        try {
            kafkaProducer.send(
                topicName = configData.paymentRequestTopicName,
                key = event.order.id.value.toString(),
                message = paymentRequest,
                callback = kafkaCallback(
                    topic = configData.paymentRequestTopicName,
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