package com.croman.kfood.order.service.domain.dto.create

import com.croman.kfood.domain.valueobject.OrderStatus
import java.util.UUID

data class CreateOrderResponse(
    val orderTrackingId: UUID,
    val orderStatus: OrderStatus, //???
    val message: String = ""
)