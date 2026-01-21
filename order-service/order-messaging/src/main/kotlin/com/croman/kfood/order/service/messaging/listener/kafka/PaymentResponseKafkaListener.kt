package com.croman.kfood.order.service.messaging.listener.kafka

import com.croman.kfood.order.service.messaging.mapper.OrderMessagingDataMapper
import com.croman.kfood.kafka.consumer.KafkaConsumer
import com.croman.kfood.kafka.order.avro.model.PaymentResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.PaymentStatus
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import com.croman.kfood.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

/**
 * Kafka consumer of the topic: `payment-response-topic`.
 * That topic is filled in with messages from the `Payment Service` when a
 * payment is completed.
 * This consumer exists in the `Order Service` because depending on the status
 * of the message: `PaymentResponseAvroModel` the Order Service will be notified
 * if the Payment was completed (`PaymentStatus.COMPLETED`) or if the Payment Failed
 * or if it was Canceled through the Message listener: `PaymentResponseMessageListener`
 */
@Component
class PaymentResponseKafkaListener(
    private val messageListener: PaymentResponseMessageListener,
    private val dataMapper: OrderMessagingDataMapper
) : KafkaConsumer<PaymentResponseAvroModel> {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(
        id = $$"${kafka-consumer-config.payment-consumer-group-id}",
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
            try {
                when (it.paymentStatus) {
                    PaymentStatus.COMPLETED -> {
                        logger.info { "Processing COMPLETED payment for order ${it.orderId}" }
                        with(dataMapper) {
                            messageListener.paymentCompleted(it.toResponse())
                        }
                    }

                    PaymentStatus.CANCELLED,
                    PaymentStatus.FAILED -> {
                        logger.info { "Processing unsuccessful (${it.paymentStatus}) payment for order ${it.orderId}" }
                        with(dataMapper) {
                            messageListener.paymentCancelled(it.toResponse())
                        }
                    }
                }
            } catch (e: OptimisticLockingFailureException) {
                logger.error(e) { "Caught an OptimisticLockingFailureException in PaymentResponseKafkaListener for order ${it.orderId}" }
                // NO-OP: Nothing else to do here, because when this exception is thrown, it means that another thread already
                // processed the PaymentResponse message. In case an exception would be thrown, the re-try mechanism
                // in kafka will try to process the same message again, something that we don't really need here.
            } catch (e: OrderNotFoundException) {
                logger.error(e) { "OrderNotFoundException in PaymentResponseKafkaListener for order ${it.orderId}" }
                // NO-OP
            }
        }
    }
}