package com.croman.kfood.dataaccess.entity

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Table(name = "products", schema = "restaurant")
@Entity
data class ProductEntity(
    @Id
    val id: UUID,
    val name: String,
    val price: BigDecimal,
    val available: Boolean,
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "restaurants")
    val restaurants: List<RestaurantEntity>,
)