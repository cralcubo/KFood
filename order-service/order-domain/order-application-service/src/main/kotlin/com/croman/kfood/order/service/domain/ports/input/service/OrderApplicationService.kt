package com.croman.kfood.order.service.domain.ports.input.service

import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.dto.create.CreateOrderResponse
import com.croman.kfood.order.service.domain.dto.track.TrackOrderQuery
import com.croman.kfood.order.service.domain.dto.track.TrackOrderResponse
import jakarta.validation.Valid

interface OrderApplicationService {
    fun createOrder(@Valid command: CreateOrderCommand): CreateOrderResponse
    fun trackOrder(query: TrackOrderQuery): TrackOrderResponse
}