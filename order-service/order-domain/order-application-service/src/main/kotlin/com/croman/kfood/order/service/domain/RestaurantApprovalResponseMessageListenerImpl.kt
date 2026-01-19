package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse
import com.croman.kfood.order.service.domain.ports.input.message.listener.approval.RestaurantApprovalResponseMessageListener
import com.croman.kfood.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import kotlin.math.log

@Service
@Validated
class RestaurantApprovalResponseMessageListenerImpl(
    private val orderApprovalSaga: OrderApprovalSaga,
//    private val orderCancelledPaymentRequestMessagePublisher: OrderCancelledPaymentRequestMessagePublisher
): RestaurantApprovalResponseMessageListener {
    private val logger = KotlinLogging.logger {}

    override fun orderApproved(response: RestaurantApprovalResponse) {
        orderApprovalSaga.processData(response)
        logger.info { "Successfully approved order ${response.orderId}" }
    }

    override fun orderRejected(response: RestaurantApprovalResponse) {
        orderApprovalSaga.rollback(response)
        logger.info { "Publishing order cancelled event for order ${response.orderId}." }
//        orderCancelledPaymentRequestMessagePublisher.publish(event)
    }
}