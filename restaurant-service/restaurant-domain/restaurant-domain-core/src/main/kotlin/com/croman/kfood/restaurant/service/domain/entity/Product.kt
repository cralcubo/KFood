package com.croman.kfood.restaurant.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import java.util.UUID

class Product private constructor(
    val id: ProductId,
    val name: String,
    val price: Money,
    val available: Boolean,
    ) : BaseEntity<ProductId>(id) {

        companion object {
            fun instantiate(id: ProductId, name: String, price: Money, available: Boolean) =
                Product(id, name, price, available)

            fun create(name: String, price: Money, available: Boolean) =
                instantiate(ProductId(UUID.randomUUID()), name, price, available)
        }
}