package com.croman.kfood.restaurant.service.messaging.mapper

import com.croman.kfood.kafka.order.avro.model.OrderApprovalStatus
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.RestaurantOrderStatus
import com.croman.kfood.restaurant.service.domain.dto.OrderProduct
import com.croman.kfood.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.croman.kfood.restaurant.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantMessagingDataMapper {

    fun OrderEventPayload.toAvroModel(sagaId: String): RestaurantApprovalResponseAvroModel =
        RestaurantApprovalResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId(sagaId)
            .setOrderId(orderId)
            .setRestaurantId(restaurantId)
            .setCreatedAt(createdAt.toInstant())
            .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderApprovalStatus))
            .setFailureMessages(failureMessages)
            .build()

    fun RestaurantApprovalRequestAvroModel.toRequest() =
        RestaurantApprovalRequest(
            id = id,
            sagaId = sagaId,
            restaurantId = restaurantId,
            orderId = orderId,
            restaurantOrderStatus = when(restaurantOrderStatus){
                RestaurantOrderStatus.PAID -> com.croman.kfood.domain.valueobject.RestaurantOrderStatus.PAID
            },
            orderProducts = products.map { OrderProduct(it.id, it.quantity) },
            price = price,
            createdAt = createdAt
        )
}