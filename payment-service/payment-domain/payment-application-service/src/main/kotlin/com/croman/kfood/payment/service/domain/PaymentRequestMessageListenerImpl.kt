package com.croman.kfood.payment.service.domain

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.payment.service.domain.dto.PaymentRequest
import com.croman.kfood.payment.service.domain.exception.PaymentApplicationServiceException
import com.croman.kfood.payment.service.domain.port.input.message.listener.PaymentRequestMessageListener
import com.croman.kfood.payment.service.domain.port.output.repository.CreditEntryRepository
import com.croman.kfood.payment.service.domain.port.output.repository.CreditHistoryRepository
import com.croman.kfood.payment.service.domain.port.output.repository.PaymentRepository
import com.croman.kfood.payment.service.domain.entity.Payment
import com.croman.kfood.payment.service.domain.event.PaymentEvent
import com.croman.kfood.payment.service.domain.mapper.PaymentDataMapper
import com.croman.kfood.payment.service.domain.outbox.scheduler.OrderOutboxHelper
import com.croman.kfood.payment.service.domain.port.output.message.publisher.PaymentResponseMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PaymentRequestMessageListenerImpl(
    private val paymentDomainService: PaymentDomainService,
    private val paymentRepository: PaymentRepository,
    private val creditHistoryRepository: CreditHistoryRepository,
    private val creditEntryRepository: CreditEntryRepository,
    private val orderOutboxHelper: OrderOutboxHelper,
    private val messagePublisher: PaymentResponseMessagePublisher,
    private val mapper: PaymentDataMapper
): PaymentRequestMessageListener {

    private val logger = KotlinLogging.logger {}

    /**
     * function called when the Order is created and is in OrderStatus.PENDING
     */
    override fun completePayment(paymentRequest: PaymentRequest) {
        // If the outbox flow is already completed, it means that the more than another request
        // got to this method, meanwhile a previous request was already successfully processed.
        // If that is the case, DO NOT restart the outbox flow and return
        if(publishIfOutboxCompletedPresent(paymentRequest, PaymentStatus.COMPLETED)) {
            logger.info { "A COMPLETED outbox message with sagaId: ${paymentRequest.sagaId} is already stored in DB" }
            return
        }

        val event = completeAndPersistPayment(paymentRequest)

        // outbox operations
        // Starting the outbox flow
        orderOutboxHelper.save(
            payload = with(mapper) { event.toPayload() },
            paymentStatus = event.toStatus(),
            outboxStatus = OutboxStatus.STARTED,
            sagaId = paymentRequest.sagaId.toUUID(),
        )
    }

    private fun publishIfOutboxCompletedPresent(request: PaymentRequest, status: PaymentStatus): Boolean {
        val completedOutboxMessage = orderOutboxHelper.getCompletedMessage(request.sagaId.toUUID(), status)
            ?: return false
        messagePublisher.publish(completedOutboxMessage) { message, status ->
            orderOutboxHelper.save(
                message.copy(outboxStatus = status)
            )
        }
        return true
    }

    private fun PaymentEvent.toStatus() =  when(this) {
        is PaymentEvent.Cancelled -> PaymentStatus.CANCELLED
        is PaymentEvent.Completed -> PaymentStatus.COMPLETED
        is PaymentEvent.Failed -> PaymentStatus.FAILED
    }

    /**
     * function called when the Order was cancelled and is in OrderStatus.CANCELLED
     */
    override fun cancelPayment(paymentRequest: PaymentRequest) {
        if(publishIfOutboxCompletedPresent(paymentRequest, PaymentStatus.CANCELLED)) {
            logger.info { "A COMPLETED outbox message with sagaId: ${paymentRequest.sagaId} is already stored in DB" }
            return
        }
        val event = cancelAndPersistPayment(paymentRequest)

        // outbox operations
        // Starting the outbox flow
        orderOutboxHelper.save(
            payload = with(mapper) { event.toPayload() },
            paymentStatus = event.toStatus(),
            outboxStatus = OutboxStatus.STARTED,
            sagaId = paymentRequest.sagaId.toUUID(),
        )
    }



    @Transactional
    fun cancelAndPersistPayment(paymentRequest: PaymentRequest) : PaymentEvent {
        logger.info { "Cancelling payment request for orderId ${paymentRequest.orderId}" }
        val orderId = OrderId(paymentRequest.orderId.toUUID())
        val payment = paymentRepository.findByOrderId(orderId) as? Payment.Completed
            ?: throw PaymentApplicationServiceException("Payment request for orderId $orderId not found")

        val customerId = CustomerId(paymentRequest.customerId.toUUID())
        val creditEntry = creditEntryRepository.findByCustomerId(customerId)
            ?: throw PaymentApplicationServiceException("Credit entry for customer ${customerId.value} not found.")

        val creditHistories = creditHistoryRepository.findByCustomerId(customerId)
            ?: throw PaymentApplicationServiceException("Credit history for customer ${customerId.value} not found.")
        // This event can only be PaymentEvent.Cancelled
        val event = paymentDomainService.cancelPayment(payment, creditEntry, creditHistories)

        paymentRepository.save(event.currentPayment)
        if(event is PaymentEvent.Cancelled) {
            logger.info { "Cancelled payment request for orderId ${paymentRequest.orderId}" }
            creditEntryRepository.save(event.currentCredit)
            creditHistoryRepository.save(event.currentCreditHistories.last())
        }
        return event
    }

    @Transactional
    private fun completeAndPersistPayment(paymentRequest: PaymentRequest): PaymentEvent {
        logger.info { "Completing payment request for orderId ${paymentRequest.orderId}" }
        val customerId = CustomerId(paymentRequest.customerId.toUUID())
        // Create a pending payment to be completed in later steps in this function!
        val payment = Payment.Pending.create(
            orderId = OrderId(paymentRequest.orderId.toUUID()),
            customerId = customerId,
            price = Money(paymentRequest.price)
        )

        val creditEntry = creditEntryRepository.findByCustomerId(customerId)
            ?: throw PaymentApplicationServiceException("Credit entry for customer ${customerId.value} not found.")

        val creditHistories = creditHistoryRepository.findByCustomerId(customerId)
            ?: throw PaymentApplicationServiceException("Credit history for customer ${customerId.value} not found.")

        // This event can be PaymentEvent.Completed or Payment.Cancelled (in case of an exception)
        val paymentEvent = paymentDomainService.completePayment(payment, creditEntry, creditHistories)
        paymentRepository.save(paymentEvent.currentPayment)
        if(paymentEvent is PaymentEvent.Completed) {
            logger.info { "Payment completed for orderId ${paymentRequest.orderId}" }
            creditEntryRepository.save(paymentEvent.currentCredit)
            creditHistoryRepository.save(paymentEvent.currentCreditHistories.last())
        }

        return paymentEvent
    }

    private fun String.toUUID() = UUID.fromString(this)
}