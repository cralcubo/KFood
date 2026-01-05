package com.croman.kfood.event

import com.croman.kfood.domain.event.DomainEvent
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.entity.OrderApproval
import com.croman.kfood.entity.Restaurant
import java.time.ZonedDateTime

sealed class OrderApprovalEvent(
    val orderApproval: OrderApproval,
    val restaurantId: RestaurantId,
    val createdAt: ZonedDateTime
): DomainEvent<OrderApproval> {
    class Approved(orderApproval: OrderApproval.Approved, restaurantId: RestaurantId, createdAt: ZonedDateTime) :
        OrderApprovalEvent(orderApproval, restaurantId, createdAt)

    class Rejected(orderApproval: OrderApproval.Rejected, restaurantId: RestaurantId, createdAt: ZonedDateTime) :
            OrderApprovalEvent(orderApproval, restaurantId, createdAt)

}