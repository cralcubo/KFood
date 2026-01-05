package com.croman.kfood.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.valueobject.OrderApprovalId
import java.util.UUID

sealed class OrderApproval private constructor(
    val id: OrderApprovalId,
    val restaurantId: RestaurantId,
    val orderId: OrderId,
    val orderApprovalStatus: OrderApprovalStatus,
) : BaseEntity<OrderApprovalId>(id) {

    class Approved(id: OrderApprovalId, restaurantId: RestaurantId, orderId: OrderId):
        OrderApproval(id, restaurantId, orderId, OrderApprovalStatus.APPROVED) {
        companion object {
            fun instantiate(id: OrderApprovalId, restaurantId: RestaurantId, orderId: OrderId) =
                Approved(id, restaurantId, orderId)
            fun create(restaurantId: RestaurantId, orderId: OrderId) =
                instantiate(OrderApprovalId(UUID.randomUUID()), restaurantId, orderId)
        }
    }

    class Rejected(id: OrderApprovalId, restaurantId: RestaurantId, orderId: OrderId):
        OrderApproval(id, restaurantId, orderId, OrderApprovalStatus.REJECTED) {

        companion object {
            fun instantiate(id: OrderApprovalId, restaurantId: RestaurantId, orderId: OrderId) =
                Rejected(id, restaurantId, orderId)

            fun create(restaurantId: RestaurantId, orderId: OrderId) =
                instantiate(OrderApprovalId(UUID.randomUUID()), restaurantId, orderId)
        }
    }
}