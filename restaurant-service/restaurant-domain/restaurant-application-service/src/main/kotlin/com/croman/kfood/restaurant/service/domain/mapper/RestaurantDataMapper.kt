package com.croman.kfood.restaurant.service.domain.mapper

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import com.croman.kfood.restaurant.service.domain.event.OrderApprovalEvent
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component

@Component
class RestaurantDataMapper {

    fun OrderApprovalEvent.toPayload() =
        OrderEventPayload(
            orderId = orderApproval.orderId.value.toString(),
            restaurantId = restaurantId.value.toString(),
            createdAt = createdAt,
            orderApprovalStatus = when(this){
                is OrderApprovalEvent.Approved -> OrderApprovalStatus.APPROVED.name
                is OrderApprovalEvent.Rejected -> OrderApprovalStatus.REJECTED.name
            } ,
            failureMessages = when(this) {
                is OrderApprovalEvent.Approved -> emptyList()
                is OrderApprovalEvent.Rejected -> listOf(failureMessage)
            }
        )
}