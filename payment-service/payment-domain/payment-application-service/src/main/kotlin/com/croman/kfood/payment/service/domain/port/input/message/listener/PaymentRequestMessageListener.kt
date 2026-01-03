package com.croman.kfood.payment.service.domain.port.input.message.listener

import com.croman.kfood.payment.service.domain.dto.PaymentRequest

interface PaymentRequestMessageListener {
    fun completePayment(paymentRequest: PaymentRequest)
    fun cancelPayment(paymentRequest: PaymentRequest)
}