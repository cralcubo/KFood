package com.croman.kfood.order.service.dataaccess.order.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.Money.Companion.ZERO
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.ProductId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.order.service.dataaccess.order.entity.OrderAddressEntity
import com.croman.kfood.order.service.dataaccess.order.entity.OrderEntity
import com.croman.kfood.order.service.dataaccess.order.entity.OrderItemEntity
import com.croman.kfood.order.service.domain.entity.ApprovedOrder
import com.croman.kfood.order.service.domain.entity.CancelledOrder
import com.croman.kfood.order.service.domain.entity.CancellingOrder
import com.croman.kfood.order.service.domain.entity.Order
import com.croman.kfood.order.service.domain.entity.OrderItem
import com.croman.kfood.order.service.domain.entity.PaidOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.entity.Product
import com.croman.kfood.order.service.domain.valueobject.OrderItemId
import com.croman.kfood.order.service.domain.valueobject.StreetAddress
import com.croman.kfood.order.service.domain.valueobject.TrackingId
import org.springframework.stereotype.Component

@Component
class OrderDataAccessMapper {

    fun Order.toEntity() =
        OrderEntity(
            id = id.value,
            customerId = customerId.value,
            restaurantId = restaurantId.value,
            trackingId = trackingId.value,
            price = price.amount,
            orderStatus = toStatus(),
            failureMessages = failureMessage(),
        ).apply {
            address = streetAddress.toEntity(this)
            items = orderItems.map { it.toEntity(this) }
        }

    fun OrderEntity.toOrder(): Order {
        val pendingOrder = toPendingOrder()

        return when (orderStatus) {
            OrderStatus.PENDING -> pendingOrder
            OrderStatus.PAID -> PaidOrder(pendingOrder)
            OrderStatus.APPROVED -> ApprovedOrder(PaidOrder(pendingOrder))
            OrderStatus.CANCELLED -> CancelledOrder(pendingOrder, failureMessages.split(","))
            OrderStatus.CANCELLING -> CancellingOrder(PaidOrder(pendingOrder), failureMessages.split(","))
        }
    }

    private fun OrderEntity.toPendingOrder(): PendingOrder {
        return PendingOrder.instantiate(
            id = OrderId(id),
            restaurantId = RestaurantId(restaurantId),
            customerId = CustomerId(customerId),
            streetAddress = address?.toAddress() ?: error("Address is null"),
            trackingId = TrackingId(trackingId),
            items = items?.map { it.toOrderItem() } ?: error("Items are null"),
        )
    }

    fun OrderItemEntity.toOrderItem() =
        OrderItem.instantiate(
            id = OrderItemId(id),
            product = Product.instantiate(ProductId(productId), "test", ZERO),
            quantity = quantity
        )


    private fun OrderAddressEntity.toAddress() =
        StreetAddress.instantiate(id, street, postalCode, city)

    fun StreetAddress.toEntity(orderEntity: OrderEntity) =
        OrderAddressEntity(
            id = id,
            order = orderEntity,
            street = street,
            city = city,
            postalCode = postalCode
        )

    fun OrderItem.toEntity(orderEntity: OrderEntity) =
        OrderItemEntity(
            id = id.value,
            order = orderEntity,
            productId = product.id.value,
            price = price.amount,
            quantity = quantity,
            subTotal = subTotal.amount,
        )

    private fun Order.failureMessage() = when (this) {
        is CancelledOrder -> failureMessages.joinToString()
        else -> ""
    }

    private fun Order.toStatus() = when (this) {
        is ApprovedOrder -> OrderStatus.APPROVED
        is CancellingOrder -> OrderStatus.CANCELLING
        is PendingOrder -> OrderStatus.PENDING
        is CancelledOrder -> OrderStatus.CANCELLED
        is PaidOrder -> OrderStatus.PAID

    }
}