package com.croman.kfood.dataaccess.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "restaurants", schema = "restaurant")
data class RestaurantEntity(
    @Id
    val id: UUID,
    val name: String,
    val active: Boolean,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinTable(
        name = "restaurant_products",
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
