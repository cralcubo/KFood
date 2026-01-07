package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener
import com.croman.kfood.order.service.domain.ports.output.message.publisher.approval.OrderPaidRestaurantRequestMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class PaymentResponseMessageListenerImpl(
    private val orderPaymentSaga: OrderPaymentSaga,
    private val orderPaidRestaurantRequestMessagePublisher: OrderPaidRestaurantRequestMessagePublisher
): PaymentResponseMessageListener {
    private val logger = KotlinLogging.logger {}

    override fun paymentCompleted(response: PaymentResponse) {
        val orderPaidEvent = orderPaymentSaga.processData(response)
        logger.info { "Publishing order paid event: $orderPaidEvent" }
        orderPaidRestaurantRequestMessagePublisher.publish(orderPaidEvent)
    }

    override fun paymentCancelled(response: PaymentResponse) {
        orderPaymentSaga.rollback(response)
        logger.info { "Order payment for order ${response.orderId} is rolled back because of: ${response.failureMessages}" }
    }
}