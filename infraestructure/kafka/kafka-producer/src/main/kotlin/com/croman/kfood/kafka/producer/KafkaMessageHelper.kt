package com.croman.kfood.kafka.producer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecordBase
import org.springframework.kafka.support.SendResult
import java.util.function.BiConsumer

class KafkaMessageHelper {

    companion object {
        private val logger = KotlinLogging.logger {}

        fun<T: SpecificRecordBase> kafkaCallback(topic: String, orderId: String, message: T)
                : BiConsumer<SendResult<String, T>, Throwable?> =
            BiConsumer { result, exception ->
                if (exception != null) {
                    logger.error(exception) { "Error while sending $message to topic: $topic " }
                } else {
                    val metadata = result.recordMetadata
                    logger.info {
                        "Received successful response from Kafka for order $orderId, " +
                                "topic: ${metadata.topic()}, " +
                                "partition: ${metadata.partition()}, " +
                                "offset: ${metadata.offset()}, " +
                                "timestamp: ${metadata.timestamp()}"
                    }
                }
            }
    }
}