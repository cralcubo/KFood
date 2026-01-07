package com.croman.kfood.order.service.domain.dto.message

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import java.time.Instant

data class RestaurantApprovalResponse(
    val id: String,
    val sagaId: String,
    val orderId: String,
    val restaurantId: String,
    val createdAt: Instant,
    val orderApprovalStatus: OrderApprovalStatus,
    val failureMessages: List<String>
)