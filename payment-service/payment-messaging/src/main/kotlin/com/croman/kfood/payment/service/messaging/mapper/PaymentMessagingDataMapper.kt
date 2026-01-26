package com.croman.kfood.payment.service.messaging.mapper

import com.croman.kfood.domain.valueobject.PaymentOrderStatus.CANCELLED
import com.croman.kfood.domain.valueobject.PaymentOrderStatus.PENDING
import com.croman.kfood.kafka.order.avro.model.PaymentOrderStatus
import com.croman.kfood.kafka.order.avro.model.PaymentRequestAvroModel
import com.croman.kfood.kafka.order.avro.model.PaymentResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.PaymentStatus
import com.croman.kfood.payment.service.domain.dto.PaymentRequest
import com.croman.kfood.payment.service.domain.event.PaymentEvent
import com.croman.kfood.payment.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PaymentMessagingDataMapper {

    fun OrderEventPayload.toAvroModel(sagaId: String): PaymentResponseAvroModel =
        PaymentResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId(sagaId)
            .setPaymentId(paymentId)
            .setCustomerId(customerId)
            .setOrderId(orderId)
            .setPrice(price)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentStatus(PaymentStatus.valueOf(paymentOrderStatus))
            .setFailureMessages(failureMessages)
            .build()

    fun PaymentEvent.Completed.toPaymentResponseAvroModel(): PaymentResponseAvroModel =
        PaymentResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setPaymentId(currentPayment.id.value.toString())
            .setCustomerId(currentPayment.customerId.value.toString())
            .setOrderId(currentPayment.orderId.value.toString())
            .setPrice(currentPayment.price.amount)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentStatus(PaymentStatus.COMPLETED)
            .setFailureMessages(emptyList())
            .build()

    fun PaymentEvent.Cancelled.toPaymentResponseAvroModel(): PaymentResponseAvroModel =
        PaymentResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setPaymentId(currentPayment.id.value.toString())
            .setCustomerId(currentPayment.customerId.value.toString())
            .setOrderId(currentPayment.orderId.value.toString())
            .setPrice(currentPayment.price.amount)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentStatus(PaymentStatus.CANCELLED)
            .setFailureMessages(emptyList())
            .build()

    fun PaymentEvent.Failed.toPaymentResponseAvroModel(): PaymentResponseAvroModel =
        PaymentResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setPaymentId(currentPayment.id.value.toString())
            .setCustomerId(currentPayment.customerId.value.toString())
            .setOrderId(currentPayment.orderId.value.toString())
            .setPrice(currentPayment.price.amount)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentStatus(PaymentStatus.FAILED)
            .setFailureMessages(listOf(failureMessage))
            .build()

    fun PaymentRequestAvroModel.toPaymentRequest() =
        PaymentRequest(
            id = id,
            sagaId = sagaId,
            orderId = orderId,
            customerId = customerId,
            price = price,
            createdAt = createdAt,
            paymentOrderStatus = when (paymentOrderStatus) {
                PaymentOrderStatus.PENDING -> PENDING
                PaymentOrderStatus.CANCELLED -> CANCELLED
            }
        )
}