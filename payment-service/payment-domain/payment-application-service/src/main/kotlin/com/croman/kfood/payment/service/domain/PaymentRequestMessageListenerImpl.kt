package com.croman.kfood.payment.service.domain

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.payment.service.domain.dto.PaymentRequest
import com.croman.kfood.payment.service.domain.exception.PaymentApplicationServiceException
import com.croman.kfood.payment.service.domain.port.input.message.listener.PaymentRequestMessageListener
import com.croman.kfood.payment.service.domain.port.output.repository.CreditEntryRepository
import com.croman.kfood.payment.service.domain.port.output.repository.CreditHistoryRepository
import com.croman.kfood.payment.service.domain.port.output.repository.PaymentRepository
import com.croman.kfood.payment.service.domain.port.output.message.publisher.PaymentCancelledMessagePublisher
import com.croman.kfood.payment.service.domain.port.output.message.publisher.PaymentCompletedMessagePublisher
import com.croman.kfood.payment.service.domain.port.output.message.publisher.PaymentFailedMessagePublisher
import com.croman.kfood.payment.service.domain.entity.Payment
import com.croman.kfood.payment.service.domain.event.PaymentEvent
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
    private val paymentCompletedMessagePublisher: PaymentCompletedMessagePublisher,
    private val paymentCancelledMessagePublisher: PaymentCancelledMessagePublisher,
    private val paymentFailedMessagePublisher: PaymentFailedMessagePublisher,
): PaymentRequestMessageListener {

    private val logger = KotlinLogging.logger {}

    override fun completePayment(paymentRequest: PaymentRequest) {
        val event = completeAndPersistPayment(paymentRequest)
        publishEvent(event)
    }

    override fun cancelPayment(paymentRequest: PaymentRequest) {
        val event = cancelAndPersistPayment(paymentRequest)
        publishEvent(event)
    }

    private fun publishEvent(event: PaymentEvent) {
        logger.info { "Publishing event ${event.javaClass.simpleName} for order ${event.currentPayment.orderId}" }
        when (event) {
            is PaymentEvent.Cancelled -> paymentCancelledMessagePublisher.publish(event)
            is PaymentEvent.Completed -> paymentCompletedMessagePublisher.publish(event)
            is PaymentEvent.Failed -> paymentFailedMessagePublisher.publish(event)
        }
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