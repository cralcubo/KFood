package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.order.service.domain.valueobject.StreetAddress
import com.croman.kfood.order.service.domain.valueobject.TrackingId
import java.util.*

sealed class Order(
    val trackingId: TrackingId,
    val id: OrderId,
    val customerId: CustomerId, // aggregate
    val restaurantId: RestaurantId, // aggregate
    val streetAddress: StreetAddress, // entity
    val orderItems: List<OrderItem>,
    val price: Money
) : AggregateRoot<OrderId>(id)

sealed class CancellableOrder(
    trackingId: TrackingId,
    id: OrderId,
    customerId: CustomerId, // aggregate
    restaurantId: RestaurantId, // aggregate
    streetAddress: StreetAddress,
    items: List<OrderItem>,
    price: Money
) : Order(trackingId, id, customerId, restaurantId, streetAddress, items, price) {

    abstract fun cancelOrder(failureMessages: List<String>): CancelledOrder

}

class PaidOrder(order: PendingOrder) :
    Order(order.trackingId, order.id, order.customerId, order.restaurantId, order.streetAddress, order.orderItems, order.price) {

    fun approveOrder() =
        ApprovedOrder(this)

    fun initCancelling(failureMessages: List<String>) =
        CancellingOrder(this, failureMessages)
}

class ApprovedOrder(order: PaidOrder) :
    Order(order.trackingId, order.id, order.customerId, order.restaurantId, order.streetAddress, order.orderItems, order.price)

class CancellingOrder(
    paidOrder: PaidOrder,
    val failureMessages: List<String>
) : CancellableOrder(
    paidOrder.trackingId,
    paidOrder.id,
    paidOrder.customerId,
    paidOrder.restaurantId,
    paidOrder.streetAddress,
    paidOrder.orderItems,
    paidOrder.price,
) {

    override fun cancelOrder(failureMessages: List<String>) =
        CancelledOrder(this, failureMessages + this.failureMessages)
}

class CancelledOrder(
    val order: CancellableOrder,
    val failureMessages: List<String>,
) : Order(order.trackingId, order.id, order.customerId, order.restaurantId, order.streetAddress, order.orderItems, order.price)

class PendingOrder private constructor(
    id: OrderId,
    customerId: CustomerId, // aggregate
    restaurantId: RestaurantId, // aggregate
    streetAddress: StreetAddress, // entity
    trackingId: TrackingId,
    items: List<OrderItem>,
    private val failureMessages: List<String> = emptyList(),
    price: Money
) : CancellableOrder(trackingId, id, customerId, restaurantId, streetAddress, items, price) {

    companion object {
        fun create(
            restaurantId: RestaurantId, customerId: CustomerId,
            streetAddress: StreetAddress, price: Money
        ) =
            instantiate(
                id = OrderId(UUID.randomUUID()),
                customerId = customerId,
                restaurantId = restaurantId,
                streetAddress = streetAddress,
                trackingId = TrackingId(UUID.randomUUID()),
                items = emptyList(),
                price = price
            )

        fun instantiate(id: OrderId,restaurantId: RestaurantId, customerId: CustomerId,
                        streetAddress: StreetAddress, trackingId: TrackingId, items: List<OrderItem>, price: Money) =
            PendingOrder(
                id = id,
                customerId = customerId,
                restaurantId = restaurantId,
                streetAddress = streetAddress,
                trackingId = trackingId,
                items = items,
                price = price
            )

    }

    fun addItems(items: List<OrderItem>) =
        copy(items = this.orderItems + items)


    fun payOrder() =
        PaidOrder(this)

    override fun cancelOrder(failureMessages: List<String>) =
        CancelledOrder(this, failureMessages)

    private fun copy(
        customerId: CustomerId = this.customerId,
        restaurantId: RestaurantId = this.restaurantId,
        streetAddress: StreetAddress = this.streetAddress,
        items: List<OrderItem> = this.orderItems,
        trackingId: TrackingId = this.trackingId,
        failureMessages: List<String> = this.failureMessages,
        price: Money = this.price
    ) = PendingOrder(
        id, customerId, restaurantId, streetAddress,  items = items,
        trackingId = trackingId, failureMessages = failureMessages, price = price
    )
}