package com.croman.kfood.order.service.domain.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.dto.create.CreateOrderResponse
import com.croman.kfood.order.service.domain.dto.create.OrderAddress
import com.croman.kfood.order.service.domain.dto.track.TrackOrderResponse
import com.croman.kfood.order.service.domain.entity.ApprovedOrder
import com.croman.kfood.order.service.domain.entity.CancelledOrder
import com.croman.kfood.order.service.domain.entity.CancellingOrder
import com.croman.kfood.order.service.domain.entity.Order
import com.croman.kfood.order.service.domain.entity.PaidOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.valueobject.StreetAddress
import org.springframework.stereotype.Component

@Component
class OrderDataMapper {

    /**
     * This method is the one that creates a new pending order, with all the basic information
     * to create one.
     */
    fun createOrder(command: CreateOrderCommand) = with(command) {
        PendingOrder.create(
            customerId = CustomerId(customerId),
            restaurantId = RestaurantId(restaurantId),
            streetAddress = address.toStreetAddress()
        )
    }

    fun orderToTrackOrderResponse(order: Order) = with(order) {
        TrackOrderResponse(
            orderTrackingId = trackingId.value,
            orderStatus = order.toOrderStatus(),
            failureMessages = when(order) {
                is CancellingOrder -> order.failureMessages
                is CancelledOrder -> order.failureMessages
                else -> emptyList()
            }
        )
    }

    fun toCreateOrderResponse(order: PendingOrder) = with(order) {
        CreateOrderResponse(
            orderTrackingId = trackingId.value,
            orderStatus = OrderStatus.PENDING,
            message = "Order created successfully",
        )
    }

    private fun Order.toOrderStatus() =
        when(this) {
            is ApprovedOrder -> OrderStatus.APPROVED
            is CancelledOrder -> OrderStatus.CANCELLED
            is CancellingOrder -> OrderStatus.CANCELLING
            is PaidOrder -> OrderStatus.PAID
            is PendingOrder -> OrderStatus.PENDING
        }

    private fun OrderAddress.toStreetAddress() =
        StreetAddress.of(
            street = street,
            postalCode = postalCode,
            city = city
        )
}