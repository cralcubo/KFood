package com.croman.kfood.restaurant.service.domain.valueobject

import com.croman.kfood.restaurant.service.domain.entity.Product

data class OrderProduct(
    val product: Product,
    val quantity: Int,
)