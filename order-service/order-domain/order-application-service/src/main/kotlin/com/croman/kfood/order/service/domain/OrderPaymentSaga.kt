package com.croman.kfood.order.service.domain

import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.entity.CancellableOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import com.croman.kfood.order.service.domain.mapper.OrderDataMapper
import com.croman.kfood.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper
import com.croman.kfood.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import com.croman.kfood.saga.SagaStep
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Component
class OrderPaymentSaga(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val mapper: OrderDataMapper
) : SagaStep<PaymentResponse> {

    val logger = KotlinLogging.logger {}
    private val getNow: ZonedDateTime
        get() = ZonedDateTime.now(ZoneId.of("UTC"))

    @Transactional
    override fun processData(data: PaymentResponse) {
        val orderPaymentOutboxMessage = paymentOutboxHelper.findBySagaIdAndSagaStatus(
            sagaId = data.sagaId.toUUID(),
            SagaStatus.STARTED
        ) ?: run {
            logger.info { "OrderPaymentOutboxMessage with saga id ${data.sagaId} for order ${data.orderId} was already processed." }
            return
        }
        // Complete the payment of the order
        logger.info { "Completing payment for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PendingOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val orderPaidEvent = orderDomainService.payOrder(order)
        orderRepository.save(orderPaidEvent.order)
        logger.info { "Order ${order.id} was paid" }
        // Update the Outbox table with an updated OrderPaymentOutboxMessage
        val updatedOrderPaymentOutboxMessage = orderPaymentOutboxMessage.copy(
            processedAt = getNow,
            sagaStatus = SagaStatus.PROCESSING,
            orderStatus = OrderStatus.PAID
        )
        paymentOutboxHelper.save(updatedOrderPaymentOutboxMessage)
        logger.info { "OrderPaymentOutboxMessage with saga id ${data.sagaId} for order ${data.orderId} was updated and saved." }
        // Create a new OrderApprovalOutboxMessage to correspond to the Approval order flow
        // Order-Service --> (order-approval-topic) --> Restaurant-Service
        approvalOutboxHelper.save(
            payload = with(mapper) { orderPaidEvent.toPayload() },
            outboxStatus = OutboxStatus.STARTED,
            orderStatus = OrderStatus.PAID,
            sagaStatus = SagaStatus.PROCESSING,
            sagaId = updatedOrderPaymentOutboxMessage.sagaId,
        )
        logger.info { "A new OrderApprovalOutboxMessage was saved for order ${data.orderId} and sagaId ${updatedOrderPaymentOutboxMessage.sagaId}" }
    }

    @Transactional
    override fun rollback(data: PaymentResponse) {
        val message = paymentOutboxHelper.findBySagaIdAndSagaStatus(
            sagaId = data.sagaId.toUUID(),
            SagaStatus.STARTED, SagaStatus.PROCESSING
        )

        logger.info { "Cancelling payment for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? CancellableOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val cancelled = orderDomainService.cancelOrder(order, data.failureMessages)
        orderRepository.save(cancelled)
        logger.info { "Order ${order.id} was cancelled" }
    }

    private fun String.toUUID() = UUID.fromString(this)

    private fun PaymentStatus.toSagaStatus(): List<SagaStatus> = when (this) {
        PaymentStatus.PENDING -> listOf(SagaStatus.STARTED)
        PaymentStatus.COMPLETED -> listOf(SagaStatus.PROCESSING) // When the payment is completed, the saga flow is still in the STARTED state. (Order was created and is in status PENDING)
        // Payment is Cancelled because the Order was Cancelled. An order is cancelled when the payment is cancelled. Now: When a payment is CANCELLED???
        PaymentStatus.CANCELLED -> listOf(SagaStatus.PROCESSING) // When the payment is cancelled, the saga flow is in the intermediate state PROCESSING.
        PaymentStatus.FAILED -> listOf(SagaStatus.PROCESSING)
    }

}