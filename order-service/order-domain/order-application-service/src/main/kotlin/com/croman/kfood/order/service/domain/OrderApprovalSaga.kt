package com.croman.kfood.order.service.domain

import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse
import com.croman.kfood.order.service.domain.entity.PaidOrder
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
import java.time.ZonedDateTime
import java.util.*
import kotlin.math.max

@Component
class OrderApprovalSaga(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository,
    private val paymentOutboxHelper: PaymentOutboxHelper,
    private val approvalOutboxHelper: ApprovalOutboxHelper,
    private val dataMapper: OrderDataMapper
) : SagaStep<RestaurantApprovalResponse> {
    private val logger = KotlinLogging.logger {}

    private val getNow: ZonedDateTime
        get() = ZonedDateTime.now()

    @Transactional
    override fun processData(data: RestaurantApprovalResponse) {
        val message = approvalOutboxHelper.getMessage(
            sagaId = data.sagaId.toUUID(),
            SagaStatus.PROCESSING // When the order is about to be approved, the SagaStatus is Processing
        )?: run {
            logger.info { "OrderApprovalOutboxMessage with saga ${data.sagaId} and order ${data.orderId} was already processed" }
            return
        }

        // Approve the order
        logger.info { "Processing restaurant approval response for order ${data.orderId}" }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PaidOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")
        val approvedOrder = orderDomainService.approveOrder(order)
        orderRepository.save(approvedOrder)
        logger.info { "Order ${order.id} was approved." }

        // update the approval outbox-message
        message.copy(
            processedAt = getNow,
            orderStatus = OrderStatus.APPROVED,
            sagaStatus = SagaStatus.SUCCEEDED
        ).let { approvalOutboxHelper.save(it) }
        // update the payment outbox-message
        val paymentMessage = paymentOutboxHelper.getMessage(
            sagaId = data.sagaId.toUUID(),
            SagaStatus.PROCESSING
        )?: throw OrderDomainException("Payment OutboxMessage with saga ${data.sagaId} and PROCESSING status not found!")
        paymentMessage.copy(
            processedAt = getNow,
            orderStatus = OrderStatus.APPROVED,
            sagaStatus = SagaStatus.SUCCEEDED
        ).let { paymentOutboxHelper.save(it) }
    }

    @Transactional
    override fun rollback(data: RestaurantApprovalResponse) {
        val message = approvalOutboxHelper.getMessage(
            sagaId = data.sagaId.toUUID(),
            SagaStatus.PROCESSING // When the order is about to be approved, the SagaStatus is Processing
        )?: run {
            logger.info { "OrderApprovalOutboxMessage with saga ${data.sagaId} and order ${data.orderId} was already rolled back!" }
            return
        }

        // Cancel the Order
        logger.info { "Cancelling order ${data.orderId} because of an error in the Restaurant Service." }
        val order = orderRepository.findByOrderId(OrderId(data.orderId.toUUID())) as? PaidOrder
            ?: throw OrderNotFoundException("Order ${data.orderId} not found")

        val event = orderDomainService.cancelOrderPayment(order, data.failureMessages)
        orderRepository.save(event.order)
        logger.info { "Order ${order.id} was cancelled." }

        // Update the Approval OutboxMessage and save it
        message.copy(
            processedAt = getNow,
            orderStatus = OrderStatus.CANCELLING,
            sagaStatus = SagaStatus.COMPENSATING
        ).let { approvalOutboxHelper.save(it) }
        // Create a new PaymentOutboxMessage because now a message needs to be picked up
        // to rollback the payment done initially
        paymentOutboxHelper.save(
            payload = with(dataMapper) { event.toPayload() },
            orderStatus = OrderStatus.CANCELLING,
            sagaStatus = SagaStatus.COMPENSATING,
            outboxStatus = OutboxStatus.STARTED,
            sagaId = data.sagaId.toUUID()
        )


    }

    private fun String.toUUID() = UUID.fromString(this)
}