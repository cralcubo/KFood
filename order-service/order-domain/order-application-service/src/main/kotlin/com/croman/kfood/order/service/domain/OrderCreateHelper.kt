package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.dto.create.CreateOrderCommand
import com.croman.kfood.order.service.domain.entity.OrderItem
import com.croman.kfood.order.service.domain.event.OrderEvent
import com.croman.kfood.order.service.domain.exception.OrderDomainException
import com.croman.kfood.order.service.domain.mapper.OrderDataMapper
import com.croman.kfood.order.service.domain.ports.output.repository.CustomerRepository
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.order.service.domain.ports.output.repository.RestaurantRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.transaction.annotation.Transactional


/**
 * Class with all the Transactional methods here!
 */
class OrderCreateHelper(
    private val orderDomainService: OrderDomainService,
    private val orderRepository: OrderRepository,
    private val restaurantRepository: RestaurantRepository,
    private val customerRepository: CustomerRepository,
    private val orderDataMapper: OrderDataMapper
) {

    private val logger = KotlinLogging.logger {}

    @Transactional
    fun persistOrder(command: CreateOrderCommand): OrderEvent.Created {
        // validate the existence of the customer
        customerRepository.findCustomer(command.customerId)
            ?: throw OrderDomainException("The customer ${command.customerId} was not found.")
        // retrieve the restaurant
        val restaurant = restaurantRepository.findRestaurant(command.restaurantId)
            ?: throw OrderDomainException("The restaurant ${command.restaurantId} was not found.")

        val order = orderDataMapper.createOrder(command)
        val availableProducts = restaurant.products.associateBy { it.id.value }
        val orderItems = command.items
            .mapNotNull {
                availableProducts[it.productId]?.let { product ->
                    OrderItem.of(product, it.quantity)
                }
            }
        orderRepository.save(order)
        logger.info { "Order ${order.id} created and persisted" }

        return orderDomainService.validateAndInitializeOrder(order, restaurant, orderItems)
    }
}