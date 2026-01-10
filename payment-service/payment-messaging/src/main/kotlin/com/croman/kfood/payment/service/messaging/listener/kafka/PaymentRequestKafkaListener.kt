package com.croman.kfood.payment.service.messaging.listener.kafka

import com.croman.kfood.kafka.consumer.KafkaConsumer
import com.croman.kfood.kafka.order.avro.model.PaymentOrderStatus
import com.croman.kfood.kafka.order.avro.model.PaymentRequestAvroModel
import com.croman.kfood.payment.service.domain.port.input.message.listener.PaymentRequestMessageListener
import com.croman.kfood.payment.service.messaging.mapper.PaymentMessagingDataMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class PaymentRequestKafkaListener(
    private val dataMapper: PaymentMessagingDataMapper,
    private val messageListener: PaymentRequestMessageListener
): KafkaConsumer<PaymentRequestAvroModel> {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(
        id = $$"${kafka-consumer-config.payment-consumer-group-id}",
        topics = [$$"${payment-service.payment-request-topic-name}"],
    )
    override fun receive(
        @Payload messages: List<PaymentRequestAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        logger.info { "${messages.size} number of payment requests received with keys: $keys, partitions: $partitions, offsets: $offsets" }
        messages.forEach {
            when(it.paymentOrderStatus) {
                PaymentOrderStatus.PENDING -> {
                    logger.info { "Processing PENDING payment request for order ${it.orderId}" }
                    with(dataMapper) { messageListener.completePayment(it.toPaymentRequest()) }
                }
                PaymentOrderStatus.CANCELLED -> {
                    logger.info { "Processing CANCELLED payment request for order ${it.orderId}" }
                    with(dataMapper) { messageListener.cancelPayment(it.toPaymentRequest()) }
                }
            }
        }
    }

}