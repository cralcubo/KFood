package com.croman.kfood.restaurant.service.domain.dto

import com.croman.kfood.domain.valueobject.RestaurantOrderStatus
import java.math.BigDecimal
import java.time.Instant

data class RestaurantApprovalRequest(
    val id: String,
    val sagaId: String,
    val restaurantId: String,
    val orderId: String,
    val restaurantOrderStatus: RestaurantOrderStatus,
    val orderProducts: List<OrderProduct>,
    val price: BigDecimal,
    val createdAt: Instant
)

data class OrderProduct(val productId: String, val quantity: Int)
