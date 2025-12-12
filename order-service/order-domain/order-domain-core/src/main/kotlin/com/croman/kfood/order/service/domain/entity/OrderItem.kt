package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.order.service.domain.valueobject.OrderItemId
import java.util.*

class OrderItem private constructor(
    val id: OrderItemId,
    val product: Product,
    private val quantity: Int
) : BaseEntity<OrderItemId>(id) {

    companion object {
        fun of(product: Product, quantity: Int) =
            OrderItem(
                id = OrderItemId(UUID.randomUUID()),
                product = product,
                quantity = quantity
            )
    }

    val price: Money
        get() = product.price

    val subTotal: Money
        get() = price.multiply(quantity)

    fun updateProduct(product: Product) =
        copy(product = product)


    fun copy(
        product: Product = this.product,
        quantity: Int = this.quantity,
    ) = of(product, quantity)

}