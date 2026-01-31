package com.croman.kfood.restaurant.service.messaging.listener.kafka

import com.croman.kfood.kafka.consumer.KafkaConsumer
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.croman.kfood.restaurant.service.domain.exception.RestaurantApplicationServiceException
import com.croman.kfood.restaurant.service.domain.exception.RestaurantNotFoundException
import com.croman.kfood.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener
import com.croman.kfood.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper
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
class RestaurantApprovalRequestKafkaListener(
    private val messageListener: RestaurantApprovalRequestMessageListener,
    private val dataMapper: RestaurantMessagingDataMapper
): KafkaConsumer<RestaurantApprovalRequestAvroModel> {
    private val logger = KotlinLogging.logger {}

    @KafkaListener(
        id = $$"${kafka-consumer-config.restaurant-approval-consumer-group-id}",
        topics = [$$"${restaurant-service.restaurant-approval-request-topic-name}"],
    )
    override fun receive(
        @Payload messages: List<RestaurantApprovalRequestAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        logger.info { "${messages.size} number of restaurant approval requests received with keys: $keys, partitions: $partitions, offsets: $offsets" }
        messages.forEach {
            try {
                logger.info { "Processing order approval for order ${it.orderId}" }
                // the restaurant order status is always PAID
                with(dataMapper) {
                    messageListener.approveOrder(it.toRequest())
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
                    throw RestaurantApplicationServiceException(
                        "There was a transactional error processing payment for order " +
                                "${it.orderId}", sqlException
                    )
                }
            } catch (e: RestaurantNotFoundException) {
                // NO-OP
                logger.error(e) { "Error processing order approval for order ${it.orderId}" }
            }
        }

    }
}