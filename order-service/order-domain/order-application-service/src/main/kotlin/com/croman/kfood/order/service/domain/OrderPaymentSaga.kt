package com.croman.kfood.order.service.domain

import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.entity.CancellableOrder
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.exception.OrderDomainException
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
        val orderPaymentOutboxMessage = paymentOutboxHelper.getMessage(
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
        val message = paymentOutboxHelper.getMessage(
            sagaId = data.sagaId.toUUID(),
            *data.paymentStatus.toSagaStatus().toTypedArray()
        ) ?: run {
            logger.info { "An outbox message with saga id: ${data.sagaId} for order: ${data.orderId} has already been rolled back!" }
            return
        }
        // Cancel the payment
        logger.info { "Cancelling payment for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? CancellableOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val cancelled = orderDomainService.cancelOrder(order, data.failureMessages)
        orderRepository.save(cancelled)
        logger.info { "Order ${order.id} was cancelled" }
        // Update the outbox message to store it later
        val updatedMessage = message.copy(
            processedAt = getNow,
            sagaStatus = SagaStatus.COMPENSATED,
            orderStatus = OrderStatus.CANCELLED
        )
        paymentOutboxHelper.save(updatedMessage)
        // When the payment status is CANCELLED (this rollback method is also called when status is FAILED)
        // update the ApprovalOutboxMessage
        if(data.paymentStatus == PaymentStatus.CANCELLED) {
            val approvalMessage = approvalOutboxHelper.getMessage(
                sagaId = data.sagaId.toUUID(),
                *data.paymentStatus.toSagaStatus().toTypedArray()
            ) ?: throw OrderDomainException("Approval OutboxMessage for saga ${data.sagaId} in COMPENSATING status not found")
            val updatedMessage = approvalMessage.copy(
                processedAt = getNow,
                orderStatus = OrderStatus.CANCELLED,
                sagaStatus = SagaStatus.COMPENSATED,
            )
            approvalOutboxHelper.save(updatedMessage)
        }
    }

    private fun String.toUUID() = UUID.fromString(this)

    private fun PaymentStatus.toSagaStatus(): List<SagaStatus> = when (this) {
        PaymentStatus.COMPLETED -> listOf(SagaStatus.STARTED) // When the payment is completed, the saga flow is still in the STARTED state. (Order was created and is in status PENDING)
        // The teacher explains in the lesson that the saga status should be in processing, however checking the saga flow drawing, it is shown that the saga status is in COMPENSATING
        // therefore using COMPENSATING instead of PROCESSING
        // When the payment is cancelled, that means that the order was not approved by the restaurant service, therefore the saga status is in Compensating
        PaymentStatus.CANCELLED -> listOf(SagaStatus.COMPENSATING) // In lecture: PROCESSING
        // Checking the actual flow a payment can only fail due to a failure in the validation of a Payment to go to COMPLETED
        // In the lecture however, the teacher says that saga status could be STARTED or PROCESSING
        PaymentStatus.FAILED -> listOf(SagaStatus.STARTED) // in lecture: STARTED, PROCESSING
        PaymentStatus.PENDING -> listOf(SagaStatus.STARTED)
    }

}