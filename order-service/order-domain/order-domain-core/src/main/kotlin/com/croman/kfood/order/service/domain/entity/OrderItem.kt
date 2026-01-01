package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.order.service.domain.valueobject.OrderItemId
import java.util.*

class OrderItem private constructor(
    val id: OrderItemId,
    val product: Product,
    val quantity: Int
) : BaseEntity<OrderItemId>(id) {

    companion object {
        
        fun create(product: Product, quantity: Int) =
            instantiate(
                id = OrderItemId(UUID.randomUUID()),
                product = product,
                quantity = quantity
            )

        fun instantiate(id: OrderItemId, product: Product, quantity: Int) =
            OrderItem(
                id = id,
                product = product,
                quantity = quantity
            )
    }

    val price: Money
        get() = product.price

    val subTotal: Money
        get() = price.multiply(quantity)

}