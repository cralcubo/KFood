package com.croman.kfood.restaurant.service.domain.valueobject

import com.croman.kfood.domain.valueobject.ProductId

data class OrderProduct(
    val productId: ProductId,
    val quantity: Int,
)