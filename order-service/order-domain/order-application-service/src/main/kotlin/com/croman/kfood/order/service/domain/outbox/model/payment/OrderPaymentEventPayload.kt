package com.croman.kfood.order.service.domain.outbox.model.payment

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderPaymentEventPayload(
    @field:JsonProperty
    val orderId: String,
    @field:JsonProperty
    val customerId: String,
    @field:JsonProperty
    val price: BigDecimal,
    @field:JsonProperty
    val createdAt: ZonedDateTime,
    @field:JsonProperty
    val paymentOrderStatus: String
)