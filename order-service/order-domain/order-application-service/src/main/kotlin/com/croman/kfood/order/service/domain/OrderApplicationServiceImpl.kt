package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.dto.create.CreateOrderResponse
import com.croman.kfood.order.service.domain.dto.track.TrackOrderQuery
import com.croman.kfood.order.service.domain.dto.track.TrackOrderResponse
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import com.croman.kfood.order.service.domain.mapper.OrderDataMapper
import com.croman.kfood.order.service.domain.ports.input.service.OrderApplicationService
import com.croman.kfood.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.order.service.domain.valueobject.TrackingId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Validated // This to enable the validation set on the interface
@Service
private class OrderApplicationServiceImpl(
    private val orderCreateHandler: OderCreateHandler,
    private val orderTrackHandler: OrderTrackHandler
): OrderApplicationService {

    override fun createOrder(command: CreateOrderCommand): CreateOrderResponse =
        orderCreateHandler.createOrder(command)

    override fun trackOrder(query: TrackOrderQuery): TrackOrderResponse =
        orderTrackHandler.trackOrder(query)
}

@Component
private class OderCreateHandler(
    private val orderDataMapper: OrderDataMapper,
    private val orderCreateHelper: OrderCreateHelper,
    private val orderCreatedEventMessagePublisher: OrderCreatedPaymentRequestMessagePublisher
) {
    private val logger = KotlinLogging.logger {}

    fun createOrder(command: CreateOrderCommand): CreateOrderResponse {
        logger.info{"Creating order for customer ${command.customerId}"}
        val event = orderCreateHelper.persistOrder(command)
        orderCreatedEventMessagePublisher.publish(event)
        return orderDataMapper.toCreateOrderResponse(event.order as PendingOrder)
    }
}

@Component
private class OrderTrackHandler(
    private val orderDataMapper: OrderDataMapper,
    private val orderRepository: OrderRepository
) {
    private val logger = KotlinLogging.logger {}

    @Transactional(readOnly = true)
    fun trackOrder(query: TrackOrderQuery): TrackOrderResponse {
        val order = orderRepository.findByTrackingId(TrackingId(query.orderTrackingId))
            ?: throw OrderNotFoundException("The order with tracking ID  ${query.orderTrackingId} does not exist")

        return orderDataMapper.orderToTrackOrderResponse(order)
    }
}