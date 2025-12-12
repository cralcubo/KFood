package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import java.util.UUID

class Product private constructor(
    val id: ProductId,
    private val name: String,
    val price: Money
) : BaseEntity<ProductId>(id) {

    companion object {
        fun of(name: String, price: Money) =
            Product(
                id = ProductId(UUID.randomUUID()),
                name = name,
                price = price
            )

    }
}