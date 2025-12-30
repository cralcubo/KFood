package com.croman.kfood.order.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order-service")
data class OrderServiceConfigData(
    val paymentRequestTopic: String,
    val paymentResponseTopic: String,
    val restaurantApprovalRequestTopic: String,
    val restaurantApprovalResponseTopic: String,
)