package com.croman.kfood.order.service.messaging.mapper

import com.croman.kfood.domain.valueobject.OrderApprovalStatus.APPROVED
import com.croman.kfood.domain.valueobject.OrderApprovalStatus.REJECTED
import com.croman.kfood.domain.valueobject.PaymentStatus.*
import com.croman.kfood.kafka.order.avro.model.*
import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.croman.kfood.order.service.domain.outbox.model.approval.OrderApprovalEventProduct
import com.croman.kfood.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderMessagingDataMapper {

    fun OrderApprovalEventPayload.toAvroModel(sagaId: String): RestaurantApprovalRequestAvroModel =
        RestaurantApprovalRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId(sagaId)
            .setRestaurantId(restaurantId)
            .setOrderId(orderId)
            .setProducts(products.map { it.toAvroModel() })
            .setPrice(price)
            .setCreatedAt(createdAt.toInstant())
            .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
            .build()

    private fun OrderApprovalEventProduct.toAvroModel() =
        com.croman.kfood.kafka.order.avro.model.Product.newBuilder()
            .setId(id)
            .setQuantity(quantity)
            .build()


    fun OrderPaymentEventPayload.toAvroModel(sagaId: String): PaymentRequestAvroModel =
        PaymentRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId(sagaId)
            .setCustomerId(customerId)
            .setOrderId(orderId)
            .setPrice(price)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentOrderStatus(PaymentOrderStatus.valueOf(paymentOrderStatus))
            .build()

    fun RestaurantApprovalResponseAvroModel.toResponse() =
        RestaurantApprovalResponse(
            id = id,
            sagaId = sagaId,
            orderId = orderId,
            restaurantId = restaurantId,
            createdAt = createdAt,
            orderApprovalStatus = when(orderApprovalStatus) {
                OrderApprovalStatus.APPROVED -> APPROVED
                OrderApprovalStatus.REJECTED -> REJECTED
            },
            failureMessages = failureMessages
        )

    fun PaymentResponseAvroModel.toResponse()=
        PaymentResponse(
            id = id,
            sagaId = sagaId,
            orderId = orderId,
            paymentId = paymentId,
            customerId = customerId,
            price = price,
            createdAt = createdAt,
            paymentStatus = when(paymentStatus){
                PaymentStatus.COMPLETED -> COMPLETED // payment was successful when an order was created (PENDING)
                PaymentStatus.CANCELLED -> CANCELLED // payment was cancelled because the order was cancelled (CANCELLED)
                PaymentStatus.FAILED -> FAILED // completing or cancelling payment Failed!
            },
            failureMessages = failureMessages
        )
}