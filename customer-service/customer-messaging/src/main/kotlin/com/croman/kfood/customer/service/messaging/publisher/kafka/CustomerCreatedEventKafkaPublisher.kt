package com.croman.kfood.customer.service.messaging.publisher.kafka

 import com.croman.kfood.customer.service.domain.config.CustomerServiceConfigData
import com.croman.kfood.customer.service.domain.event.CustomerCreatedEvent
import com.croman.kfood.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher
import com.croman.kfood.customer.service.messaging.mapper.CustomerMessagingDataMapper
import com.croman.kfood.kafka.order.avro.model.CustomerAvroModel
import com.croman.kfood.kafka.producer.service.KafkaProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

@Component
class CustomerCreatedEventKafkaPublisher(
    private val kafkaProducer: KafkaProducer<String, CustomerAvroModel>,
    private val dataMapper: CustomerMessagingDataMapper,
    private val configData: CustomerServiceConfigData
): CustomerMessagePublisher {
    private val logger = KotlinLogging.logger {}

    override fun publish(event: CustomerCreatedEvent) {
        logger.info { "Received CustomerCreatedEvent for customer ${event.customer.id.value}" }

        try {
            val avroCustomer = with(dataMapper) {event.toAvroModel()}
            kafkaProducer.send(
                topicName = configData.customerTopicName,
                key = avroCustomer.id.toString(),
                message = avroCustomer,
            ) { result, exception ->
                if(exception != null) {
                    logger.error(exception) { "Error while sending CustomerCreatedEvent for customer ${event.customer.id.value} to kafka" }
                } else {
                    val metadata = result.recordMetadata
                    logger.info {
                        "Received new metadata. " +
                                "Topic: ${metadata.topic()}, " +
                                "Partition: ${metadata.partition()}, " +
                                "Offset: ${metadata.offset()}, " +
                                "Timestamp: ${metadata.timestamp()}, " +
                                "Time: ${System.nanoTime()}"
                    }
                }

            }
        } catch (exception: Exception) {
            logger.error(exception) { "Error while sending CustomerCreatedEvent for customer ${event.customer.id.value} to kafka" }
        }
    }
}