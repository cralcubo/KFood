package com.croman.kfood.order.service.messaging.listener.kafka

import com.croman.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croman.kfood.kafka.consumer.KafkaConsumer
import com.croman.kfood.kafka.order.avro.model.PaymentResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.PaymentStatus
import com.croman.kfood.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class PaymentResponseKafkaListener(
    private val messageListener: PaymentResponseMessageListener,
    private val dataMapper: OrderMessagingDataMapper
): KafkaConsumer<PaymentResponseAvroModel> {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(
        id= $$"${kafka-consumer-config.payment-consumer-group-id}",
        topics = [$$"${order-service.payment-response-topic-name}"]
    )
    override fun receive(
        @Payload messages: List<PaymentResponseAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        logger.info { "${messages.size} number of payment responses received with keys: $keys, partitions: $partitions, offsets: $offsets" }
        // process all the messages
        messages.forEach {
            when(it.paymentStatus) {
                PaymentStatus.COMPLETED -> {
                    logger.info { "Processing successful payment for order ${it.orderId}" }
                    with(dataMapper) {
                        messageListener.paymentCompleted(it.toResponse())
                    }
                }
                PaymentStatus.CANCELLED,
                PaymentStatus.FAILED -> {
                    logger.info { "Processing unsuccessful payment for order ${it.orderId}" }
                    with(dataMapper) {
                        messageListener.paymentCancelled(it.toResponse())
                    }
                }
            }
        }
    }
}