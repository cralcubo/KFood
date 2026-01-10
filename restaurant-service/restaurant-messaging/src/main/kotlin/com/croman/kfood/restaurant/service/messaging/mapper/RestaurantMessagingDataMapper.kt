package com.croman.kfood.restaurant.service.messaging.mapper

import com.croman.kfood.kafka.order.avro.model.OrderApprovalStatus
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.croman.kfood.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.croman.kfood.kafka.order.avro.model.RestaurantOrderStatus
import com.croman.kfood.restaurant.service.domain.dto.OrderProduct
import com.croman.kfood.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.croman.kfood.restaurant.service.domain.event.OrderApprovalEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantMessagingDataMapper {

    fun OrderApprovalEvent.toAvroModel(): RestaurantApprovalResponseAvroModel =
        RestaurantApprovalResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setSagaId("")
            .setOrderId(orderApproval.orderId.value.toString())
            .setRestaurantId(restaurantId.value.toString())
            .setCreatedAt(createdAt.toInstant())
            .setOrderApprovalStatus(when(this){
                is OrderApprovalEvent.Approved -> OrderApprovalStatus.APPROVED
                is OrderApprovalEvent.Rejected -> OrderApprovalStatus.REJECTED
            })
            .setFailureMessages(emptyList())
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