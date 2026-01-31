package com.croman.kfood.restaurant.service.domain.outbox.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderEventPayload(
    @field:JsonProperty
    val orderId: String,

    @field:JsonProperty
    val restaurantId: String,

    @field:JsonProperty
    val createdAt: ZonedDateTime,

    @field:JsonProperty
    val orderApprovalStatus: String,

    @field:JsonProperty
    val failureMessages: List<String>,
)