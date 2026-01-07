package com.croman.kfood.order.service.domain.ports.output.repository

import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.order.service.domain.entity.Order
import com.croman.kfood.order.service.domain.valueobject.TrackingId

interface OrderRepository {
    fun save(order: Order): Order
    fun findByTrackingId(trackingId: TrackingId): Order?
    fun findByOrderId(orderId: OrderId): Order?
}