package com.croman.kfood.order.service.domain.ports.input.message.listener.payment

import com.croman.kfood.order.service.domain.dto.message.PaymentResponse

interface PaymentResponseMessageListener {
    fun paymentCompleted(response: PaymentResponse)
    fun paymentCancelled(response: PaymentResponse)
}