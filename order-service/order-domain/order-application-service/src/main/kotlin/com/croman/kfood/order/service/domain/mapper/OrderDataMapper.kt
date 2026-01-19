package com.croman.kfood.order.service.domain.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.PaymentOrderStatus
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.domain.valueobject.RestaurantOrderStatus
import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.dto.create.CreateOrderResponse
import com.croman.kfood.order.service.domain.dto.create.OrderAddress
import com.croman.kfood.order.service.domain.dto.track.TrackOrderResponse
import com.croman.kfood.order.service.domain.entity.ApprovedOrder
import com.croman.kfood.order.service.domain.entity.CancelledOrder
import com.croman.kfood.order.service.domain.entity.CancellingOrder
import com.croman.kfood.order.service.domain.entity.Order
import com.croman.kfood.order.service.domain.entity.OrderItem
import com.croman.kfood.order.service.domain.entity.PaidOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalEventProduct
import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import com.croman.kfood.order.service.domain.valueobject.StreetAddress
import org.springframework.stereotype.Component

@Component
class OrderDataMapper {

    /**
     * This method is the one that creates a new pending order, with all the basic information
     * to create one.
     */
    fun createPendingOrder(command: CreateOrderCommand) = with(command) {
        PendingOrder.create(
            customerId = CustomerId(customerId),
            restaurantId = RestaurantId(restaurantId),
            streetAddress = address.toStreetAddress(),
            price = Money(command.paidAmount)
        )
    }

    fun orderToTrackOrderResponse(order: Order) = with(order) {
        TrackOrderResponse(
            orderTrackingId = trackingId.value,
            orderStatus = order.toOrderStatus(),
            failureMessages = when (order) {
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
            message = "Order ${order.id} created successfully",
        )
    }

    fun OrderEvent.Created.toPayload() =
        OrderPaymentEventPayload(
            orderId = order.id.value.toString(),
            customerId = order.customerId.value.toString(),
            price = order.price.amount,
            createdAt = createdAt,
            paymentOrderStatus = PaymentOrderStatus.PENDING.name
        )

    fun OrderEvent.Paid.toPayload() =
        OrderApprovalEventPayload(
            orderId = order.id.value.toString(),
            restaurantId = order.restaurantId.value.toString(),
            price = order.price.amount,
            createdAt = createdAt,
            restaurantOrderStatus = RestaurantOrderStatus.PAID.name,
            products = order.orderItems.map { it.toOrderApprovalProducts() }
        )

    fun OrderEvent.Cancelled.toPayload() =
        OrderPaymentEventPayload(
            orderId = order.id.value.toString(),
            customerId = order.customerId.value.toString(),
            price = order.price.amount,
            createdAt = createdAt,
            paymentOrderStatus = PaymentOrderStatus.CANCELLED.name,
        )

    private fun OrderItem.toOrderApprovalProducts() =
        OrderApprovalEventProduct(
            id = product.id.value.toString(),
            quantity = quantity
        )


    private fun Order.toOrderStatus() =
        when (this) {
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