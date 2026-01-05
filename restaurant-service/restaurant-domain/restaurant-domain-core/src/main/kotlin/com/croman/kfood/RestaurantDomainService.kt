package com.croman.kfood

import com.croman.kfood.entity.OrderDetail
import com.croman.kfood.entity.Restaurant
import com.croman.kfood.event.OrderApprovalEvent
import com.croman.kfood.exception.RestaurantDomainException
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.ZoneId
import java.time.ZonedDateTime

interface RestaurantDomainService {
    fun validateOrder(restaurant: Restaurant, orderDetail: OrderDetail): OrderApprovalEvent
}

class RestaurantDomainServiceImpl : RestaurantDomainService {

    private val logger = KotlinLogging.logger {}

    override fun validateOrder(restaurant: Restaurant, orderDetail: OrderDetail): OrderApprovalEvent {
        val now = ZonedDateTime.now(ZoneId.of("UTC"))

        return try {
            restaurant.validateOrder(orderDetail)
            // the order is valid, create an order approval
            logger.info { "Order ${orderDetail.orderId} was approved!" }
            val orderApproval = restaurant.approveOrder(orderDetail)
            OrderApprovalEvent.Approved(orderApproval, restaurant.id, now)
        } catch (e: RestaurantDomainException) {
            logger.error(e) { "The order ${orderDetail.orderId} is invalid. Order rejected!" }
            val rejectOrder = restaurant.rejectOrder(orderDetail)
            OrderApprovalEvent.Rejected(rejectOrder, restaurant.id, now)
        }
    }

}