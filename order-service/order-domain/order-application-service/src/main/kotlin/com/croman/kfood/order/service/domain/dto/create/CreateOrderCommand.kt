package com.croman.kfood.order.service.domain.dto.create

import jakarta.validation.constraints.Max
import java.math.BigDecimal
import java.util.*

/**
 * This command will be sent by the client.
 * This command contains:
 * - The [customerId] because a user that wants to submit an order, must be an existent user in the system.
 * - The [restaurantId] because if the user will submit an order, it must correspond to a product (food) sold by this restaurant.
 *   Therefore, the restaurant must exist in the system too.
 * - The [paidAmount] is the total price paid when submitting the order.
 * - The [items] are the products the customer wants to order.
 * - The [address] is the delivery address.
 *
 */
data class CreateOrderCommand(
    val customerId: UUID,
    val restaurantId: UUID,
    val items: List<OrderItem>,
    val address: OrderAddress,
    val paidAmount: BigDecimal,
)

/**
 * The order item to be purchased by the customer.
 * - The [productId] is the ID of the product that makes this item.
 * - The [quantity] is the number of the same product to buy.
 * - The [price_shouldBeRemoved] as above this is weird. May need to be removed
 * - The [subTotal_shouldBeRemoved] is weird to have it here too.
 */
data class OrderItem(
    val productId: UUID,
    val quantity: Int,

    val subTotal_shouldBeRemoved: BigDecimal? = null,
    val price_shouldBeRemoved: BigDecimal? = null,
)

data class OrderAddress(
    @param:Max(value = 50)
    val street: String,
    @param:Max(value = 10)
    val postalCode: String,
    @param:Max(value = 50)
    val city: String
)