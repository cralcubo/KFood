package com.croman.kfood.payment.service.domain.mapper

import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.payment.service.domain.event.PaymentEvent
import com.croman.kfood.payment.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component

@Component
class PaymentDataMapper {

    fun PaymentEvent.toPayload() =
        OrderEventPayload(
            paymentId = currentPayment.id.value.toString(),
            orderId = currentPayment.orderId.value.toString(),
            customerId = currentPayment.customerId.value.toString(),
            price = currentPayment.price.amount,
            createdAt = createdAt,
            paymentOrderStatus = when(this){
                is PaymentEvent.Cancelled -> PaymentStatus.CANCELLED.name
                is PaymentEvent.Completed -> PaymentStatus.COMPLETED.name
                is PaymentEvent.Failed -> PaymentStatus.FAILED.name
            },
            failureMessages = when(this) {
                is PaymentEvent.Failed -> listOf(failureMessage)
                else -> emptyList()
            }
        )

}