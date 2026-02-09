package com.croman.kfood.customer.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "customer-service")
data class CustomerServiceConfigData(
    val customerTopicName: String
)