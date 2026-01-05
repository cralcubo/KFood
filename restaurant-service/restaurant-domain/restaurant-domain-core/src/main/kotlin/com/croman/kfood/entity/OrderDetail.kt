package com.croman.kfood.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus

class OrderDetail(
    val orderId: OrderId,
    val orderStatus: OrderStatus,
    val totalAmount: Money,
    val products: List<Product>
) : BaseEntity<OrderId>(orderId) {

    companion object {
        fun instantiate(id: OrderId, orderStatus: OrderStatus, totalAmount: Money, products: List<Product>) =
            OrderDetail(id, orderStatus, totalAmount, products)

//        fun create(orderStatus: OrderStatus, totalAmount: Money, products: List<Product>) =
//            instantiate(OrderId(UUID.randomUUID()), orderStatus, totalAmount, products)
    }
}