package com.croman.kfood.restaurant.service.domain

import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.ProductId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.domain.valueobject.RestaurantOrderStatus
import com.croman.kfood.outbox.OutboxStatus
import com.croman.kfood.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.croman.kfood.restaurant.service.domain.entity.OrderDetail
import com.croman.kfood.restaurant.service.domain.event.OrderApprovalEvent
import com.croman.kfood.restaurant.service.domain.exception.RestaurantDomainException
import com.croman.kfood.restaurant.service.domain.exception.RestaurantNotFoundException
import com.croman.kfood.restaurant.service.domain.mapper.RestaurantDataMapper
import com.croman.kfood.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper
import com.croman.kfood.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener
import com.croman.kfood.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher
import com.croman.kfood.restaurant.service.domain.ports.output.repository.OrderApprovalRepository
import com.croman.kfood.restaurant.service.domain.ports.output.repository.RestaurantRepository
import com.croman.kfood.restaurant.service.domain.valueobject.OrderProduct
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RestaurantApprovalRequestMessageListenerImpl(
    private val restaurantDomainService: RestaurantDomainService,
    private val restaurantRepository: RestaurantRepository,
    private val orderApprovalRepository: OrderApprovalRepository,
    private val messagePublisher: RestaurantApprovalResponseMessagePublisher,
    private val orderOutboxHelper: OrderOutboxHelper,
    private val dataMapper: RestaurantDataMapper
) : RestaurantApprovalRequestMessageListener {

    private val logger = KotlinLogging.logger {}

    override fun approveOrder(request: RestaurantApprovalRequest) {
        if(publishIfOutboxMessageProcessed(request)) {
            logger.info { "Outbox message with sagaId: ${request.sagaId} is already saved in database" }
            return
        }

        val event = approveAndPersistOrder(request)
        when (event) {
            is OrderApprovalEvent.Approved -> {
                logger.info { "Order ${request.orderId} is approved. Publishing Order-Approved Event." }
            }

            is OrderApprovalEvent.Rejected -> {
                logger.info { "Order ${request.orderId} was rejected. Publishing Order-Rejected Event." }
            }
        }
        orderOutboxHelper.save(
            payload = with(dataMapper) { event.toPayload() },
            approvalStatus = when (event) {
                is OrderApprovalEvent.Approved -> OrderApprovalStatus.APPROVED
                is OrderApprovalEvent.Rejected -> OrderApprovalStatus.REJECTED
            },
            outboxStatus = OutboxStatus.STARTED,
            sagaId = request.sagaId.toUUID()
        )
    }

    private fun publishIfOutboxMessageProcessed(request: RestaurantApprovalRequest): Boolean {
        val completedMessage = orderOutboxHelper.getCompletedOutboxMessage(
            sagaId = request.sagaId.toUUID(),
            outboxStatus = OutboxStatus.COMPLETED,
        ) ?: return false

        messagePublisher.publish(completedMessage) { message, status ->
            orderOutboxHelper.save(
                message.copy(outboxStatus = status)
            )
        }
        return true
    }

    @Transactional
    private fun approveAndPersistOrder(request: RestaurantApprovalRequest): OrderApprovalEvent {
        logger.info { "Received approve order request for order ${request.orderId}" }
        val restaurant = restaurantRepository.findById(RestaurantId(request.restaurantId.toUUID()))
            ?: throw RestaurantNotFoundException(
                "Restaurant ${request.restaurantId} not found"
            )
        if (!restaurant.active) {
            throw RestaurantDomainException("Requested restaurant ${request.restaurantId} is not active")
        }

        val orderDetail = OrderDetail.instantiate(
            orderId = OrderId(request.orderId.toUUID()),
            orderStatus = when (request.restaurantOrderStatus) {
                RestaurantOrderStatus.PAID -> OrderStatus.PAID
            },
            totalAmount = Money(request.price),
            orderProducts = request.orderProducts.map {
                OrderProduct(ProductId(it.productId.toUUID()), it.quantity)
            }
        )
        val event = restaurantDomainService.validateOrder(restaurant, orderDetail)
        orderApprovalRepository.save(event.orderApproval)
        return event
    }

    private fun String.toUUID(): UUID = UUID.fromString(this)
}