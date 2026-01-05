package com.croman.kfood.dataaccess.restaurant.service.domain.dto

import com.croman.kfood.domain.valueobject.RestaurantOrderStatus
import com.croman.kfood.entity.Product
import java.math.BigDecimal
import java.time.Instant

data class RestaurantApprovalRequest(
    val id: String,
    val sagaId: String,
    val restaurantId: String,
    val orderId: String,
    val restaurantOrderStatus: RestaurantOrderStatus,
    val products: List<Product>, // change to productsId???
    val price: BigDecimal,
    val createdAt: Instant
)