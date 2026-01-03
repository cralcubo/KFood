package com.croman.kfood.payment.service.messaging.publisher.kafka

import com.croman.kfood.kafka.order.avro.model.PaymentResponseAvroModel
import com.croman.kfood.kafka.producer.KafkaMessageHelper
import com.croman.kfood.kafka.producer.service.KafkaProducer
import com.croman.kfood.payment.service.domain.config.PaymentServiceConfigData
import com.croman.kfood.payment.service.domain.event.PaymentEvent
import com.croman.kfood.payment.service.domain.port.output.repository.message.publisher.PaymentCancelledMessagePublisher
import com.croman.kfood.payment.service.domain.port.output.repository.message.publisher.PaymentCompletedMessagePublisher
import com.croman.kfood.payment.service.messaging.mapper.PaymentMessagingDataMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class PaymentCancelledKafkaMessagePublisher(
    private val dataMapper: PaymentMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, PaymentResponseAvroModel>, // sends message to the payment-response-topic
    private val configData: PaymentServiceConfigData
) : PaymentCancelledMessagePublisher {
    private val logger = KotlinLogging.logger {}

    override fun publish(event: PaymentEvent.Cancelled) {
        val orderId = event.currentPayment.orderId
        logger.info { "Publishing payment cancelled event for order $orderId" }
        val paymentResponseAvro = with(dataMapper) { event.toPaymentResponseAvroModel() }
        try {
            kafkaProducer.send(
                topicName = configData.paymentResponseTopicName,
                key = event.currentPayment.id.value.toString(),
                message = paymentResponseAvro,
                callback = KafkaMessageHelper.kafkaCallback(
                    topic = configData.paymentResponseTopicName,
                    orderId = orderId.value.toString(),
                    message = paymentResponseAvro
                )
            )
            logger.info { "Successfully published PaymentResponseAvroModel for order $orderId" }
        } catch (e: Exception) {
            logger.error { "Failed to publish PaymentResponseAvroModel for order $orderId: ${e.message}" }
        }

    }
}