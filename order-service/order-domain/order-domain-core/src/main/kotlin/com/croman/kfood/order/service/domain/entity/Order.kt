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
    val orderItems: List<OrderItem>
) : AggregateRoot<OrderId>(id) {

    val price: Money
        get() = orderItems.map { it.subTotal }
            .reduce { acc, money -> acc.add(money) }
}

sealed class CancellableOrder(
    trackingId: TrackingId,
    id: OrderId,
    customerId: CustomerId, // aggregate
    restaurantId: RestaurantId, // aggregate
    streetAddress: StreetAddress,
    items: List<OrderItem>,
) : Order(trackingId, id, customerId, restaurantId, streetAddress, items) {

    abstract fun cancelOrder(failureMessages: List<String>): CancelledOrder

}

class PaidOrder(order: PendingOrder) :
    Order(order.trackingId, order.id, order.customerId, order.restaurantId, order.streetAddress, order.orderItems) {

    fun approveOrder() =
        ApprovedOrder(this)

    fun initCancelling(failureMessages: List<String>) =
        CancellingOrder(this, failureMessages)
}

class ApprovedOrder(order: PaidOrder) :
    Order(order.trackingId, order.id, order.customerId, order.restaurantId, order.streetAddress, order.orderItems)

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
) {

    override fun cancelOrder(failureMessages: List<String>) =
        CancelledOrder(this, failureMessages + this.failureMessages)
}

class CancelledOrder(
    val order: CancellableOrder,
    val failureMessages: List<String>,
) : Order(order.trackingId, order.id, order.customerId, order.restaurantId, order.streetAddress, order.orderItems)

class PendingOrder private constructor(
    id: OrderId,
    customerId: CustomerId, // aggregate
    restaurantId: RestaurantId, // aggregate
    streetAddress: StreetAddress, // entity
    trackingId: TrackingId,
    items: List<OrderItem>,
    private val failureMessages: List<String> = emptyList()
) : CancellableOrder(trackingId, id, customerId, restaurantId, streetAddress, items) {

    companion object {
        fun create(
            restaurantId: RestaurantId, customerId: CustomerId,
            streetAddress: StreetAddress
        ) =
            instantiate(
                id = OrderId(UUID.randomUUID()),
                customerId = customerId,
                restaurantId = restaurantId,
                streetAddress = streetAddress,
                trackingId = TrackingId(UUID.randomUUID()),
                items = emptyList()
            )

        fun instantiate(id: OrderId,restaurantId: RestaurantId, customerId: CustomerId,
                        streetAddress: StreetAddress, trackingId: TrackingId, items: List<OrderItem>) =
            PendingOrder(
                id = id,
                customerId = customerId,
                restaurantId = restaurantId,
                streetAddress = streetAddress,
                trackingId = trackingId,
                items = items
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
        failureMessages: List<String> = this.failureMessages
    ) = PendingOrder(
        id, customerId, restaurantId, streetAddress,  items = items,
        trackingId = trackingId, failureMessages = failureMessages
    )
}