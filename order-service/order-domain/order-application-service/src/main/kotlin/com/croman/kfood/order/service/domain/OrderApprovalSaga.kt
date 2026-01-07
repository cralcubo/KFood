package com.croman.kfood.order.service.domain

import com.croman.kfood.domain.event.EmptyEvent
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse
import com.croman.kfood.order.service.domain.entity.CancellableOrder
import com.croman.kfood.order.service.domain.entity.PaidOrder
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.saga.SagaStep
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

class OrderApprovalSaga(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository
) : SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderEvent.Cancelled> {
    private val logger = KotlinLogging.logger {}

    @Transactional
    override fun processData(data: RestaurantApprovalResponse): EmptyEvent {
        logger.info { "Processing restaurant approval response for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PaidOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val approvedOrder = orderDomainService.approveOrder(order)
        orderRepository.save(approvedOrder)
        logger.info { "Order ${order.id} was approved." }
        return EmptyEvent()
    }

    @Transactional
    override fun rollback(data: RestaurantApprovalResponse): OrderEvent.Cancelled {
        logger.info { "Cancelling order ${data.orderId} because of an error in the Restaurant Service." }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PaidOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")

        val event = orderDomainService.cancelOrderPayment(order, data.failureMessages)
        orderRepository.save(event.order)
        logger.info { "Order ${order.id} was cancelled." }
        return event
    }

    private fun String.toUUID() = UUID.fromString(this)
}