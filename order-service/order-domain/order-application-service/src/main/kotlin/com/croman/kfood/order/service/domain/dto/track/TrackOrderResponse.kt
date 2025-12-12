package com.croman.kfood.order.service.domain.dto.track

import com.croman.kfood.domain.valueobject.OrderStatus
import java.util.UUID

data class TrackOrderResponse(
    val orderTrackingId: UUID,
    val orderStatus: OrderStatus, //???
    val failureMessages: List<String>
)