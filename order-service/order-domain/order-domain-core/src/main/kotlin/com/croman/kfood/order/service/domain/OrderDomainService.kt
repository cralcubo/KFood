package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.entity.ApprovedOrder
import com.croman.kfood.order.service.domain.entity.CancellableOrder
import com.croman.kfood.order.service.domain.entity.CancelledOrder
import com.croman.kfood.order.service.domain.entity.OrderItem
import com.croman.kfood.order.service.domain.entity.PaidOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.entity.Restaurant
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.exception.OrderDomainException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Check lecture 17 on why the ODS is created in the core and not in the application-service
 */
interface OrderDomainService {

    fun validateAndInitializeOrder(order: PendingOrder, restaurant: Restaurant, orderItems: List<OrderItem>): OrderEvent.Created

    fun payOrder(order: PendingOrder): OrderEvent.Paid

    fun approveOrder(order: PaidOrder): ApprovedOrder

    fun cancelOrderPayment(order: PaidOrder, failureMessages: List<String>): OrderEvent.Cancelled

    fun cancelOrder(order: CancellableOrder, failureMessages: List<String>): CancelledOrder
}

class OrderDomainServiceImpl: OrderDomainService {

    private val logger = KotlinLogging.logger {}

    private val now
        get() = ZonedDateTime.now(ZoneId.of("UTC"))


    override fun validateAndInitializeOrder(
        order: PendingOrder,
        restaurant: Restaurant,
        orderItems: List<OrderItem>
    ): OrderEvent.Created {
        validateRestaurant(restaurant)
        val pendingOrder = order.addItems(orderItems)
        logger.info { "Order ${pendingOrder.id} was initiated." }
        return OrderEvent.Created(pendingOrder, now)
    }

    override fun payOrder(order: PendingOrder): OrderEvent.Paid {
        val paidOrder = order.payOrder()
        logger.info { "Order $paidOrder was paid" }
        return OrderEvent.Paid(paidOrder, now)
    }

    override fun approveOrder(order: PaidOrder): ApprovedOrder {
        val approvedOrder = order.approveOrder()
        logger.info { "Order was approved $approvedOrder" }
        return approvedOrder
    }

    override fun cancelOrderPayment(order: PaidOrder, failureMessages: List<String>): OrderEvent.Cancelled {
        val cancellingOrder = order.initCancelling(failureMessages)
        logger.info { "Order $cancellingOrder is cancelling" }
        return OrderEvent.Cancelled(cancellingOrder, now)
    }

    override fun cancelOrder(
        order: CancellableOrder,
        failureMessages: List<String>
    ): CancelledOrder {
        val cancelledOrder = order.cancelOrder(failureMessages)
        logger.info { "Cancelled order $cancelledOrder with failure message: $failureMessages" }
        return cancelledOrder
    }

    private fun validateRestaurant(restaurant: Restaurant) {
        if(!restaurant.active) {
            throw OrderDomainException("The restaurant is not active")
        }
    }

}