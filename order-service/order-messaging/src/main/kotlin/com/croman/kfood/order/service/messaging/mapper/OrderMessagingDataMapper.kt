package com.croman.kfood.order.service.messaging.mapper

import com.croman.kfood.domain.valueobject.OrderApprovalStatus.APPROVED
import com.croman.kfood.domain.valueobject.OrderApprovalStatus.REJECTED
import com.croman.kfood.domain.valueobject.PaymentStatus.CANCELLED
import com.croman.kfood.domain.valueobject.PaymentStatus.COMPLETED
import com.croman.kfood.domain.valueobject.PaymentStatus.FAILED
import com.croman.kfood.kafka.order.avro.model.OrderApprovalStatus
import com.croman.kfood.kafka.order.avro.model.PaymentOrderStatus
import com.croman.kfood.kafka.order.avro.model.PaymentRequestAvroModel
import com.croman.kfood.kafka.order.avro.model.PaymentResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.PaymentStatus
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.RestaurantOrderStatus
import com.croman.kfood.order.service.domain.dto.message.PaymentResponse
import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse
import com.croman.kfood.order.service.domain.entity.Product
import com.croman.kfood.order.service.domain.event.OrderEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
class OrderMessagingDataMapper {

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
                PaymentStatus.COMPLETED -> COMPLETED
                PaymentStatus.CANCELLED -> CANCELLED
                PaymentStatus.FAILED -> FAILED
            },
            failureMessages = failureMessages
        )

    fun OrderEvent.Created.toAvroModel(): PaymentRequestAvroModel =
        PaymentRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setCustomerId(order.customerId.value.toString())
            .setOrderId(order.id.value.toString())
            .setPrice(order.price.amount)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
            .build()

    fun OrderEvent.Cancelled.toAvroModel(): PaymentRequestAvroModel =
        PaymentRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setCustomerId(order.customerId.value.toString())
            .setOrderId(order.id.value.toString())
            .setPrice(order.price.amount)
            .setCreatedAt(createdAt.toInstant())
            .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
            .build()

    fun OrderEvent.Paid.toAvroModel(): RestaurantApprovalRequestAvroModel =
        RestaurantApprovalRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setRestaurantId(order.restaurantId.value.toString())
            .setOrderId(order.id.value.toString())
            .setProducts(order.orderItems.map { it.product.toAvroModel(it.quantity) })
            .setPrice(order.price.amount)
            .setCreatedAt(createdAt.toInstant())
            .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
            .build()

    private fun Product.toAvroModel(quantity: Int) =
        com.croman.kfood.kafka.order.avro.model.Product.newBuilder()
            .setId(id.value.toString())
            .setQuantity(quantity)
            .build()
}