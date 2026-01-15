package com.croman.kfood.order.service.domain

import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.dto.create.CreateOrderResponse
import com.croman.kfood.order.service.domain.dto.track.TrackOrderQuery
import com.croman.kfood.order.service.domain.dto.track.TrackOrderResponse
import com.croman.kfood.order.service.domain.entity.PendingOrder
import com.croman.kfood.order.service.domain.exception.OrderNotFoundException
import com.croman.kfood.order.service.domain.mapper.OrderDataMapper
import com.croman.kfood.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper
import com.croman.kfood.order.service.domain.ports.input.service.OrderApplicationService
import com.croman.kfood.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.order.service.domain.valueobject.TrackingId
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.saga.SagaStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.util.UUID

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
    private val paymentOutboxHelper: PaymentOutboxHelper
) {
    private val logger = KotlinLogging.logger {}

    /**
     * The following is a Transactional operation because we want to SAVE the
     * new Order AND the Payment OutboxMessage atomically.
     */
    @Transactional
    fun createOrder(command: CreateOrderCommand): CreateOrderResponse {
        logger.info{"Creating order for customer ${command.customerId}"}
        val event = orderCreateHelper.createAndPersistPendingOrder(command)
        val response = orderDataMapper.toCreateOrderResponse(event.order as PendingOrder)
        val payload = with(orderDataMapper) { event.toPayload() }

        val sagaId = UUID.randomUUID()
        logger.info { "Starting the saga flow with ID: $sagaId for order ${event.order.id}" }
        // The following is where the SAGA flow starts!
        paymentOutboxHelper.save(
            payload = payload,
            orderStatus = OrderStatus.PENDING,
            sagaStatus = SagaStatus.STARTED,
            outboxStatus = OutboxStatus.STARTED,
            sagaId = sagaId,
        )
        return response

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