package com.croman.kfood.restaurant.service.domain.event

import com.croman.kfood.domain.event.DomainEvent
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.restaurant.service.domain.entity.OrderApproval
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