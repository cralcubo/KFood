package com.croman.kfood.order.service.domain.dto.message

import com.croman.kfood.domain.valueobject.PaymentStatus
import java.math.BigDecimal
import java.time.Instant

data class PaymentResponse(
    val id: String,
    val sagaId: String,
    val orderId: String,
    val paymentId: String,
    val customerId: String,
    val price: BigDecimal,
    val createdAt: Instant,
    val paymentStatus: PaymentStatus,
    val failureMessages: List<String>,

)