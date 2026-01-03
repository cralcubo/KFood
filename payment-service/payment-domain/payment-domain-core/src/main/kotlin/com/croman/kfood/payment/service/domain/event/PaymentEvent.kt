package com.croman.kfood.payment.service.domain.event

import com.croman.kfood.domain.event.DomainEvent
import com.croman.kfood.payment.service.domain.entity.CreditEntry
import com.croman.kfood.payment.service.domain.entity.CreditHistory
import com.croman.kfood.payment.service.domain.entity.Payment
import java.time.ZonedDateTime

sealed class PaymentEvent(
    val currentPayment: Payment,
    val currentCredit: CreditEntry,
    val currentCreditHistories: List<CreditHistory>,
    val createdAt: ZonedDateTime
): DomainEvent<Payment> {

    class Completed(payment: Payment.Completed, currentCredit: CreditEntry, currentCreditHistories: List<CreditHistory>, createdAt: ZonedDateTime) :
        PaymentEvent(payment, currentCredit, currentCreditHistories, createdAt)

    class Failed(payment: Payment.Failed, currentCredit: CreditEntry, currentCreditHistories: List<CreditHistory>, createdAt: ZonedDateTime, val message: String) :
        PaymentEvent(payment, currentCredit, currentCreditHistories, createdAt)

    class Cancelled(payment: Payment.Cancelled, currentCredit: CreditEntry, currentCreditHistories: List<CreditHistory>, createdAt: ZonedDateTime) :
        PaymentEvent(payment, currentCredit, currentCreditHistories, createdAt)
}