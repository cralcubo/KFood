package com.croman.kfood.order.service.domain.event

import com.croman.kfood.domain.event.DomainEvent
import com.croman.kfood.order.service.domain.entity.CancellableOrder
import com.croman.kfood.order.service.domain.entity.Order
import com.croman.kfood.order.service.domain.entity.PaidOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import java.time.ZonedDateTime

sealed class OrderEvent(
    val order: Order,
    val createdAt: ZonedDateTime
) : DomainEvent<Order> {
     class Paid(order: PaidOrder, createdAt: ZonedDateTime) : OrderEvent(order, createdAt)

    class Created(order: PendingOrder, createdAt: ZonedDateTime) : OrderEvent(order, createdAt)

    class Cancelling(order: CancellableOrder, createdAt: ZonedDateTime) : OrderEvent(order, createdAt)
}


