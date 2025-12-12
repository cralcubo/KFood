package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.order.service.domain.valueobject.StreetAddress
import com.croman.kfood.order.service.domain.valueobject.TrackingId
import java.util.*

sealed interface Order {
    val trackingId: TrackingId
}

interface CancellableOrder : Order {
    fun cancelOrder(failureMessages: List<String>): CancelledOrder
}

class PaidOrder(override val trackingId: TrackingId,) : Order {
    fun approve() =
        ApprovedOrder(trackingId)

    fun initCancelling(failureMessages: List<String>) =
        CancellingOrder(trackingId, failureMessages)
}

class ApprovedOrder(override val trackingId: TrackingId,) : Order

class CancellingOrder(
    override val trackingId: TrackingId,
    val failureMessages: List<String>
) : Order, CancellableOrder {

    override fun cancelOrder(failureMessages: List<String>) =
        CancelledOrder(trackingId, failureMessages + this.failureMessages)
}

class CancelledOrder(
    override val trackingId: TrackingId,
    val failureMessages: List<String>
) : Order

class PendingOrder private constructor(
    val id: OrderId,
    private val customerId: CustomerId, // aggregate
    private val restaurantId: RestaurantId, // aggregate
    private val streetAddress: StreetAddress, // entity

    val items: List<OrderItem> = emptyList(),
    override val trackingId: TrackingId = TrackingId(UUID.randomUUID()),
    private val price: Money = Money.ZERO,
    private val failureMessages: List<String> = emptyList()
) : AggregateRoot<OrderId>(id), Order, CancellableOrder {

    companion object {
        fun of(
            restaurantId: RestaurantId, customerId: CustomerId,
            streetAddress: StreetAddress
        ) =
            PendingOrder(
                id = OrderId(UUID.randomUUID()),
                customerId = customerId,
                restaurantId = restaurantId,
                streetAddress = streetAddress
            )

    }

    fun addItems(items: List<OrderItem>) =
        copy(items = this.items + items)


    fun updateItems(items: List<OrderItem>) =
        copy(items = items)

    fun pay() =
        PaidOrder(trackingId)

    override fun cancelOrder(failureMessages: List<String>) =
        CancelledOrder(trackingId, failureMessages)

    private fun copy(
        customerId: CustomerId = this.customerId,
        restaurantId: RestaurantId = this.restaurantId,
        streetAddress: StreetAddress = this.streetAddress,
        price: Money = this.price,
        items: List<OrderItem> = this.items,
        trackingId: TrackingId = this.trackingId,
        failureMessages: List<String> = this.failureMessages
    ) = PendingOrder(
        id, customerId, restaurantId, streetAddress,  price =price, items = items,
        trackingId = trackingId, failureMessages = failureMessages
    )


}