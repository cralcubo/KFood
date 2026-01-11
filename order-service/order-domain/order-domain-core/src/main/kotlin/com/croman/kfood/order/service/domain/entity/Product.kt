package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId

class Product private constructor(
    val id: ProductId,
    val price: Money
) : BaseEntity<ProductId>(id) {

    companion object {

        fun instantiate(id: ProductId, price: Money) =
            Product(
                id = id,
                price = price
            )
    }
}