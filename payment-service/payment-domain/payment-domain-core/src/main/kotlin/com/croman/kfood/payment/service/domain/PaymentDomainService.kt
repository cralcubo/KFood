package com.croman.kfood.payment.service.domain

import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.payment.service.domain.entity.CreditEntry
import com.croman.kfood.payment.service.domain.entity.CreditHistory
import com.croman.kfood.payment.service.domain.entity.Payment
import com.croman.kfood.payment.service.domain.event.PaymentEvent
import com.croman.kfood.payment.service.domain.exception.CreditException
import com.croman.kfood.payment.service.domain.exception.PaymentDomainException
import com.croman.kfood.payment.service.domain.valueobject.TransactionType
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.ZoneId
import java.time.ZonedDateTime

interface PaymentDomainService {

    fun completePayment(
        payment: Payment.Pending,
        creditEntry: CreditEntry,
        creditHistories: List<CreditHistory>
    ): PaymentEvent

    fun cancelPayment(
        payment: Payment.Completed,
        creditEntry: CreditEntry,
        creditHistories: List<CreditHistory>
    ): PaymentEvent
}

class PaymentDomainServiceImpl : PaymentDomainService {

    private val logger = KotlinLogging.logger {}

    override fun completePayment(
        payment: Payment.Pending,
        creditEntry: CreditEntry,
        creditHistories: List<CreditHistory>
    ): PaymentEvent {
        val now = ZonedDateTime.now(ZoneId.of("UTC"))
        return try {
            payment.validatePayment()
            validateCreditEntry(payment, creditEntry)
            val updatedCredit = creditEntry.subtractCredit(payment.price)
            val updatedHistories = updateCreditHistory(payment, creditHistories, TransactionType.DEBIT)
            validateCreditHistories(updatedCredit, updatedHistories)
            PaymentEvent.Completed(payment.complete(), updatedCredit, updatedHistories, now)
        } catch (e: Exception) {
            PaymentEvent.Failed(
                payment.failed(),
                creditEntry,
                creditHistories,
                now,
                e.message ?: "Unknown error (${e.javaClass.simpleName})"
            )
        }
    }


    override fun cancelPayment(
        payment: Payment.Completed,
        creditEntry: CreditEntry,
        creditHistories: List<CreditHistory>
    ): PaymentEvent {
        logger.info { "Cancelling payment for order ${payment.orderId}" }
        val now = ZonedDateTime.now(ZoneId.of("UTC"))

        val updatedCredit = creditEntry.addCredit(payment.price)
        val updatedHistories = updateCreditHistory(payment, creditHistories, TransactionType.CREDIT)

        return PaymentEvent.Cancelled(payment.cancel(), updatedCredit, updatedHistories, now)
    }

    private fun validateCreditHistories(creditEntry: CreditEntry, creditHistories: List<CreditHistory>) {
        val totalCreditAmount = creditHistories.filter { it.transactionType == TransactionType.CREDIT }
            .map { it.amount }
            .reduce(Money::add)

        val totalDebitAmount = creditHistories.filter { it.transactionType == TransactionType.DEBIT }
            .map { it.amount }
            .reduce(Money::add)

        // validations
        val customerId = creditEntry.customerId
        if (totalDebitAmount.isGreaterThan(totalCreditAmount)) {
            logger.error { "Customer $customerId does not have enough credit according to its credit history." }
            throw CreditException.Insufficient("Customer $customerId does not have enough credit according to its credit history.")
        }
        if (creditEntry.totalCreditAmount != totalCreditAmount.subtract(totalDebitAmount)) {
            logger.error { "Credit history does not match current credit for customer $customerId." }
            throw CreditException.Invalid("Credit history does not match current credit for customer $customerId.")
        }

    }


    private fun validateCreditEntry(payment: Payment.Pending, creditEntry: CreditEntry) {
        if (payment.price.isGreaterThan(creditEntry.totalCreditAmount)) {
            logger.error { "Customer ${payment.customerId.value} does not have enough credit." }
            throw CreditException.Insufficient("Customer ${payment.customerId.value} does not have enough credit.")
        }
    }

    private fun updateCreditHistory(
        payment: Payment,
        histories: List<CreditHistory>,
        type: TransactionType
    ): List<CreditHistory> {
        val newCredit = CreditHistory.create(payment.customerId, payment.price, type)
        return histories + newCredit
    }

}