package com.croman.kfood.payment.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "payment-service")
data class PaymentServiceConfigData(
    val paymentRequestTopicName: String,
    val paymentResponseTopicName: String
)