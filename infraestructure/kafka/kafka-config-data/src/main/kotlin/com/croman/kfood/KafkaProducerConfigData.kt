package com.croman.kfood

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
class KafkaProducerConfigData(
    val keySerializerClass: String,
    val valueSerializerClass: String,
    val compressionType: String,
    val acks: String,
    val batchSize: Int,
    val batchSizeBoostFactor: Int,
    val lingerMs: Int,
    val requestTimeoutMs: Int,
    val retryCount: Int
)