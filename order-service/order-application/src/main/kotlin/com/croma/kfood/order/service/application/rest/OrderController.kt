package com.croma.kfood.order.service.application.rest

import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.dto.create.CreateOrderResponse
import com.croman.kfood.order.service.domain.dto.track.TrackOrderQuery
import com.croman.kfood.order.service.domain.dto.track.TrackOrderResponse
import com.croman.kfood.order.service.domain.ports.input.service.OrderApplicationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("orders", produces = ["application/vnd.api+json"])
class OrderController(
    private val service: OrderApplicationService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody command: CreateOrderCommand): CreateOrderResponse {
        logger.info{ "Creating order for costumer ${command.customerId} at restaurant ${command.restaurantId}" }
        val response =  service.createOrder(command)
        logger.info{ "Order created successfully for ${command.customerId} with tracking ID ${response.orderTrackingId}" }
        return response
    }

    @GetMapping("/{trackingId}")
    fun getOrder(@PathVariable trackingId: UUID) : TrackOrderResponse {
        val response = service.trackOrder(TrackOrderQuery(trackingId))
        logger.info{ "Getting order for tracking ID ${response.orderTrackingId} with status ${response.orderStatus}" }
        return response
    }
}