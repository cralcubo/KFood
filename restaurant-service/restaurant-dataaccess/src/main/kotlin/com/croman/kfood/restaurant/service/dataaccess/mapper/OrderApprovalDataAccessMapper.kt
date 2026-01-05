package com.croman.kfood.restaurant.service.dataaccess.mapper

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.restaurant.service.dataaccess.entity.OrderApprovalEntity
import com.croman.kfood.restaurant.service.domain.entity.OrderApproval
import com.croman.kfood.restaurant.service.domain.valueobject.OrderApprovalId
import org.springframework.stereotype.Component

@Component
class OrderApprovalDataAccessMapper {

    fun OrderApprovalEntity.toOrderApproval(): OrderApproval =
        when(orderApprovalStatus) {
            OrderApprovalStatus.APPROVED -> OrderApproval.Approved.instantiate(
                id = OrderApprovalId(id),
                restaurantId = RestaurantId(restaurantId),
                orderId = OrderId(orderId),
            )
            OrderApprovalStatus.REJECTED -> OrderApproval.Rejected.instantiate(
                id = OrderApprovalId(id),
                restaurantId = RestaurantId(restaurantId),
                orderId = OrderId(orderId),
            )
        }


    fun OrderApproval.toEntity() = OrderApprovalEntity(
        id = id.value,
        restaurantId = restaurantId.value,
        orderId = orderId.value,
        orderApprovalStatus = when(this) {
            is OrderApproval.Approved -> OrderApprovalStatus.APPROVED
            is OrderApproval.Rejected -> OrderApprovalStatus.REJECTED
        }
    )
}