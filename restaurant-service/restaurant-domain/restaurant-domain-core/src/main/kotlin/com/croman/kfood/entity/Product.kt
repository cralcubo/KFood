package com.croman.kfood.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import java.util.UUID

class Product private constructor(
    val id: ProductId,
    val name: String,
    val price: Money,
    val quantity: Int,
    val available: Boolean,
    ) : BaseEntity<ProductId>(id) {

        companion object {
            fun instantiate(id: ProductId, name: String, price: Money, quantity: Int, available: Boolean) =
                Product(id, name, price, quantity, available)

            fun create(name: String, price: Money, quantity: Int, available: Boolean) =
                instantiate(ProductId(UUID.randomUUID()), name, price, quantity, available)
        }
}