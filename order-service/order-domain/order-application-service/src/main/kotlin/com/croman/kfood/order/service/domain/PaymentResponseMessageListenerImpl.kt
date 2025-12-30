package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class PaymentResponseMessageListenerImpl: PaymentResponseMessageListener {

    override fun paymentCompleted(response: PaymentResponse) {
        TODO("Not yet implemented")
    }

    override fun paymentCancelled(response: PaymentResponse) {
        TODO("Not yet implemented")
    }
}