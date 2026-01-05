package com.croman.kfood.restaurant.service.domain

import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.OrderStatus
import com.croman.kfood.domain.valueobject.ProductId
import com.croman.kfood.domain.valueobject.RestaurantOrderStatus
import com.croman.kfood.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.croman.kfood.restaurant.service.domain.entity.OrderDetail
import com.croman.kfood.restaurant.service.domain.event.OrderApprovalEvent
import com.croman.kfood.restaurant.service.domain.exception.RestaurantDomainException
import com.croman.kfood.restaurant.service.domain.exception.RestaurantNotFoundException
import com.croman.kfood.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener
import com.croman.kfood.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher
import com.croman.kfood.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher
import com.croman.kfood.restaurant.service.domain.ports.output.repository.OrderApprovalRepository
import com.croman.kfood.restaurant.service.domain.ports.output.repository.RestaurantRepository
import com.croman.kfood.restaurant.service.domain.valueobject.OrderProduct
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RestaurantApprovalRequestMessageListenerImpl(
    val restaurantDomainService: RestaurantDomainService,
    val restaurantRepository: RestaurantRepository,
    val orderApprovalRepository: OrderApprovalRepository,
    val orderApprovedMessagePublisher: OrderApprovedMessagePublisher,
    val orderRejectedMessagePublisher: OrderRejectedMessagePublisher
): RestaurantApprovalRequestMessageListener {

    private val logger = KotlinLogging.logger {}

    override fun approveOrder(request: RestaurantApprovalRequest) {
        when(val event = approveAndPersistOrder(request)){
            is OrderApprovalEvent.Approved -> {
                logger.info { "Order ${request.orderId} is approved!" }
                orderApprovedMessagePublisher.publish(event)
            }
            is OrderApprovalEvent.Rejected -> {
                logger.info { "Order ${request.orderId} was rejected!" }
                orderRejectedMessagePublisher.publish(event)
            }
        }
    }

    @Transactional
    private fun approveAndPersistOrder(request: RestaurantApprovalRequest): OrderApprovalEvent {
        logger.info { "Received approve order request for order ${request.orderId}" }
        val restaurant = restaurantRepository.findById(request.restaurantId.toUUID())
            ?: throw RestaurantNotFoundException(
                "Restaurant ${request.restaurantId} not found"
            )
        if(!restaurant.active) {
            throw RestaurantDomainException("Requested restaurant ${request.restaurantId} is not active")
        }

        val orderDetail = OrderDetail.instantiate(
            id = OrderId(request.orderId.toUUID()),
            orderStatus = when(request.restaurantOrderStatus) {
                RestaurantOrderStatus.PAID -> OrderStatus.PAID
            },
            totalAmount = Money(request.price),
            orderProducts = request.orderProducts.map {
                OrderProduct(ProductId(UUID.fromString(it.productId)), it.quantity)
            }
        )
        val event = restaurantDomainService.validateOrder(restaurant, orderDetail)
        orderApprovalRepository.save(event.orderApproval)
        return event
    }

    private fun String.toUUID(): UUID = UUID.fromString(this)
}