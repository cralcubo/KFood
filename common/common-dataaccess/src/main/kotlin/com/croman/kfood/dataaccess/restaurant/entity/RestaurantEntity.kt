package com.croman.kfood.dataaccess.restaurant.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.math.BigDecimal
import java.util.UUID

@Table(name = "order_restaurant_m_view", schema = "restaurant")
@Entity
@IdClass(RestaurantEntityId::class)
data class RestaurantEntity(
    @Id
    val restaurantId: UUID,
    @Id
    val productId: UUID,
    val restaurantName: String,
    val restaurantActive: Boolean,
    val productName: String,
    val productPrice: BigDecimal,
    val productAvailable: Boolean,
)

/**
 * Set default values when the class is instantiated without arguments.
 * Something apparently needed by jpa.
 */
data class RestaurantEntityId(
    val restaurantId: UUID = UUID.randomUUID(),
    val productId: UUID = UUID.randomUUID()
): Serializable