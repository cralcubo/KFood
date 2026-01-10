package com.croman.kfood.restaurant.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.restaurant.service.domain.exception.RestaurantDomainException

class Restaurant private constructor(
    val id: RestaurantId,
    val active: Boolean,
    val products: List<Product>
    ) : AggregateRoot<RestaurantId >(id) {

    companion object {
        fun instantiate(id: RestaurantId, active: Boolean, products: List<Product>) =
            Restaurant(
                id = id,
                active = active,
                products = products
            )
    }

    fun approveOrder(orderDetail: OrderDetail) =
        OrderApproval.Approved.create(id, orderDetail.orderId)

    fun rejectOrder(orderDetail: OrderDetail) =
        OrderApproval.Rejected.create(id, orderDetail.orderId)

    fun validateOrder(orderDetail: OrderDetail) {
        if(orderDetail.orderStatus != OrderStatus.PAID) {
            throw RestaurantDomainException("The order ${orderDetail.orderId.value} is not paid.")
        }

        // Check that the order matches the available products and that are available
        orderDetail.orderProducts.forEach { orderProduct ->
            val restaurantProduct = products.find { it.id == orderProduct.productId }
                ?: throw RestaurantDomainException("The product ${orderProduct.productId} was not found in restaurant $id.")
            if(!restaurantProduct.available) {
                throw RestaurantDomainException("The product ${orderProduct.productId} is not available in restaurant $id.")
            }
        }

        val total = orderDetail.orderProducts
            .map {
                val product = products.find { restaurantProduct -> restaurantProduct.id == it.productId }!!
                product.price.multiply(it.quantity)
            }
            .reduce(Money::add)

        if(total != orderDetail.totalAmount) {
            throw RestaurantDomainException("Price total is not correct for order ${orderDetail.orderId.value}")
        }
    }
}