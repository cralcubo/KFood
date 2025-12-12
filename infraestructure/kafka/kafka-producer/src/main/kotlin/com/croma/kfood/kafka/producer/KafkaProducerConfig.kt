package com.croma.kfood.kafka.producer

import com.croman.kfood.KafkaConfigData
import com.croman.kfood.KafkaProducerConfigData
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.io.Serializable


@Configuration
class KafkaProducerConfig<K : Serializable, V : SpecificRecordBase>(
    private val kafkaConfigData: KafkaConfigData,
    private val kafkaProducerConfigData: KafkaProducerConfigData
) {

    @Bean
    fun producerConfig() =
        mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaConfigData.bootstrapServers,
            kafkaConfigData.schemaRegistryUrlKey to kafkaConfigData.schemaRegistryUrl,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to kafkaProducerConfigData.keySerializerClass,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to kafkaProducerConfigData.valueSerializerClass,
            ProducerConfig.BATCH_SIZE_CONFIG to kafkaProducerConfigData.batchSize * kafkaProducerConfigData.batchSizeBoostFactor,
            ProducerConfig.LINGER_MS_CONFIG to kafkaProducerConfigData.lingerMs,
            ProducerConfig.COMPRESSION_TYPE_CONFIG to kafkaProducerConfigData.compressionType,
            ProducerConfig.ACKS_CONFIG to kafkaProducerConfigData.acks,
            ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG to kafkaProducerConfigData.requestTimeoutMs,
            ProducerConfig.RETRIES_CONFIG to kafkaProducerConfigData.retryCount
        )

    @Bean
    fun producerFactory(): ProducerFactory<K, V> =
        DefaultKafkaProducerFactory(producerConfig())

    @Bean
    fun kafkaTemplate(): KafkaTemplate<K, V> =
        KafkaTemplate(producerFactory())

}