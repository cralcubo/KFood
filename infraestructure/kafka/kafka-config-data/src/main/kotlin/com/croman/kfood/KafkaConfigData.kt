package com.croman.kfood

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka-config")
data class KafkaConfigData(
    val bootstrapServers: String,
    val schemaRegistryUrlKey: String,
    val schemaRegistryUrl: String,
    val numOfPartitions: Int,
    val replicationFactor: Short,
)