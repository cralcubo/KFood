package com.croman.kfood.order.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "order-service")
data class OrderServiceConfigData(
    val paymentRequestTopicName: String,
    val paymentResponseTopicName: String,
    val restaurantApprovalRequestTopicName: String,
    val restaurantApprovalResponseTopicName: String,
)