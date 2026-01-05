package com.croman.kfood.dataaccess.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.io.Serializable
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "restaurant_product",
        schema = "restaurant",
        joinColumns = [JoinColumn(name = "restaurant_id")],
        inverseJoinColumns = [JoinColumn(name = "product_id")]
    )
    val products: List<ProductEntity>
)

/**
 * Set default values when the class is instantiated without arguments.
 * Something apparently needed by jpa.
 */
data class RestaurantEntityId(
    val restaurantId: UUID = UUID.randomUUID(),
    val productId: UUID = UUID.randomUUID()
): Serializable
