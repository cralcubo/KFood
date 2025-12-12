package com.croma.kfood.kafka.producer.service

import com.croma.kfood.kafka.producer.exception.KafkaProducerException
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PreDestroy
import org.apache.avro.specific.SpecificRecordBase
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.function.BiConsumer

interface KafkaProducer<K : Serializable?, V : SpecificRecordBase?> {
    fun send(topicName: String, key: K, message: V, callback: BiConsumer<SendResult<K, V>, Throwable>)
}

@Component
class KafkaProducerImpl<K : Serializable?, V : SpecificRecordBase?>(
    private val kafkaTemplate: KafkaTemplate<K,V>
): KafkaProducer<K, V> {

    private val logger = KotlinLogging.logger {}

    override fun send(
        topicName: String,
        key: K,
        message: V,
        callback: BiConsumer<SendResult<K, V>, Throwable>
    ) {
        logger.info { "Sending message $message to topic $topicName." }
        try {
        // Send the message to Kafka
        val kafkaResultFuture = kafkaTemplate.send(topicName, key, message)
        // Asynchronously notify that the message was completed to the callback function
        kafkaResultFuture.whenComplete(callback)
        } catch (e: Exception) {
            logger.error(e) { "Failed to send message to topic $topicName, with key $key and message $message." }
            throw KafkaProducerException("There was an error sending the message $message to the topic $topicName with key $key")
        }
    }

    @PreDestroy
    fun close() {
        logger.info { "Closing kafka producer." }
        kafkaTemplate.destroy()
    }

}