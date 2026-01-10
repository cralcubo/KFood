package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import java.util.UUID

class Product private constructor(
    val id: ProductId,
    val name: String,
    val price: Money
) : BaseEntity<ProductId>(id) {

    companion object {

        fun instantiate(id: ProductId, name: String, price: Money) =
            Product(
                id = id,
                name = name,
                price = price
            )

    }
}