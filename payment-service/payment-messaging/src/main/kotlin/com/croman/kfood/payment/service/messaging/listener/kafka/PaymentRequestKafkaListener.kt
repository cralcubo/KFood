package com.croman.kfood.payment.service.messaging.listener.kafka

import com.croman.kfood.kafka.consumer.KafkaConsumer
import com.croman.kfood.kafka.order.avro.model.PaymentOrderStatus
import com.croman.kfood.kafka.order.avro.model.PaymentRequestAvroModel
import com.croman.kfood.payment.service.domain.exception.PaymentApplicationServiceException
import com.croman.kfood.payment.service.domain.exception.PaymentNotFoundException
import com.croman.kfood.payment.service.domain.port.input.message.listener.PaymentRequestMessageListener
import com.croman.kfood.payment.service.messaging.mapper.PaymentMessagingDataMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.postgresql.util.PSQLState
import org.springframework.dao.DataAccessException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.sql.SQLException

@Component
class PaymentRequestKafkaListener(
    private val dataMapper: PaymentMessagingDataMapper,
    private val messageListener: PaymentRequestMessageListener
) : KafkaConsumer<PaymentRequestAvroModel> {
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
            try {
                when (it.paymentOrderStatus) {
                    PaymentOrderStatus.PENDING -> { // Status when the Order is Created
                        logger.info { "Processing PENDING payment request for order ${it.orderId}" }
                        with(dataMapper) { messageListener.completePayment(it.toPaymentRequest()) }
                    }

                    PaymentOrderStatus.CANCELLED -> { // Status when the Order is Cancelled
                        logger.info { "Processing CANCELLED payment request for order ${it.orderId}" }
                        with(dataMapper) { messageListener.cancelPayment(it.toPaymentRequest()) }
                    }
                }
            } catch (e: DataAccessException) {
                val sqlException = e.rootCause as? SQLException
                if (sqlException != null && sqlException.sqlState != null
                    && sqlException.sqlState == PSQLState.UNIQUE_VIOLATION.state
                ) {
                    // NO-OP
                    logger.error { "Caught a unique constraint exception with sql state: ${sqlException.sqlState} " +
                            "in PaymentRequestKafkaListener for order ${it.orderId}" }
                } else {
                    throw PaymentApplicationServiceException("There was a transactional error processing payment for order " +
                            "${it.orderId}", sqlException)
                }
            } catch (e: PaymentNotFoundException) {
                // NO-OP
                logger.error { "Payment not found for order ${it.orderId}" }
            }
        }
    }

}