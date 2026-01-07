package com.croman.kfood.order.service.domain

import com.croman.kfood.domain.event.EmptyEvent
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.saga.SagaStep
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class OrderPaymentSaga(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository
) : SagaStep<PaymentResponse, OrderEvent.Paid, EmptyEvent> {

    val logger = KotlinLogging.logger {}

    @Transactional
    override fun processData(data: PaymentResponse): OrderEvent.Paid {
        logger.info { "Completing payment for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PendingOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val orderPaidEvent = orderDomainService.payOrder(order)
        orderRepository.save(orderPaidEvent.order)
        logger.info { "Order ${order.id} was paid" }
        return orderPaidEvent
    }

    @Transactional
    override fun rollback(data: PaymentResponse): EmptyEvent {
        logger.info { "Cancelling payment for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PendingOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val cancelled = orderDomainService.cancelOrder(order, data.failureMessages)
        orderRepository.save(cancelled)
        logger.info { "Order ${order.id} was cancelled" }
        return EmptyEvent()
    }

    private fun String.toUUID() = UUID.fromString(this)

}