package com.croman.kfood.kafka.producer

import com.croman.kfood.outbox.OutboxStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.support.SendResult
import java.util.function.BiConsumer

class KafkaMessageHelper {

    companion object {
        private val logger = KotlinLogging.logger {}

        fun<T, U> kafkaCallback(
            topic: String,
            orderId: String,
            avroMessage: T,
            outboxCallback: BiConsumer<U, OutboxStatus>,
            outboxMessage: U
        )
                : BiConsumer<SendResult<String, T>, Throwable?> =
            BiConsumer { result, exception ->
                if (exception != null) {
                    logger.error(exception) { "Error while sending $avroMessage to topic: $topic " }
                    outboxCallback.accept(outboxMessage, OutboxStatus.FAILED)
                } else {
                    val metadata = result.recordMetadata
                    logger.info {
                        "Received successful response from Kafka for order $orderId, " +
                                "topic: ${metadata.topic()}, " +
                                "partition: ${metadata.partition()}, " +
                                "offset: ${metadata.offset()}, " +
                                "timestamp: ${metadata.timestamp()}"
                    }
                    outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED)
                }
            }
    }
}