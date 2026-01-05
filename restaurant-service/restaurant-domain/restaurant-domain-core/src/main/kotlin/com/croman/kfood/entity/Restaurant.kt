package com.croman.kfood.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.exception.RestaurantDomainException
import io.github.oshai.kotlinlogging.KotlinLogging

class Restaurant private constructor(
    val id: RestaurantId,
    val active: Boolean,
    val products: List<Product>
//    val orderApproval: OrderApproval,
//    val orderDetail: OrderDetail
    ) : AggregateRoot<RestaurantId >(id) {

    companion object {
        fun instantiate(id: RestaurantId, active: Boolean, products: List<Product>) =
            Restaurant(
                id = id,
                active = active,
                products = products
            )
    }

    private val logger = KotlinLogging.logger {}

    fun approveOrder(orderDetail: OrderDetail) =
        OrderApproval.Approved.create(id, orderDetail.orderId)

    fun rejectOrder(orderDetail: OrderDetail) =
        OrderApproval.Rejected.create(id, orderDetail.orderId)

    fun validateOrder(orderDetail: OrderDetail) {
        if(orderDetail.orderStatus != OrderStatus.PAID) {
            throw RestaurantDomainException("The order ${orderDetail.orderId.value} is not paid.")
        }

        // Check that the order matches the available products and that they are available
        orderDetail.products.forEach { orderProduct ->
            val restaurantProduct = products.find { restaurantProduct -> restaurantProduct == orderProduct }
                ?: throw RestaurantDomainException("The product ${orderDetail.orderId} was not found in restaurant $id.")

            if(!restaurantProduct.available) {
                throw RestaurantDomainException("The product ${orderDetail.orderId} is not available in restaurant $id.")
            }
        }

        val total = orderDetail.products
            .map { it.price.multiply(it.quantity) }
            .reduce(Money::add)

        if(total != orderDetail.totalAmount) {
            throw RestaurantDomainException("Price total is not correct for order ${orderDetail.orderId.value}")
        }
    }
}