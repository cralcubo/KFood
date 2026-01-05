package com.croman.kfood.restaurant.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.restaurant.service.domain.entity.Product
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.restaurant.service.domain.valueobject.OrderProduct

class OrderDetail(
    val orderId: OrderId,
    val orderStatus: OrderStatus,
    val totalAmount: Money,
    val orderProducts: List<OrderProduct>,
) : BaseEntity<OrderId>(orderId) {

    companion object {
        fun instantiate(id: OrderId, orderStatus: OrderStatus, totalAmount: Money, orderProducts: List<OrderProduct>) =
            OrderDetail(id, orderStatus, totalAmount,  orderProducts)

//        fun create(orderStatus: OrderStatus, totalAmount: Money, products: List<Product>) =
//            instantiate(OrderId(UUID.randomUUID()), orderStatus, totalAmount, products)
    }
}