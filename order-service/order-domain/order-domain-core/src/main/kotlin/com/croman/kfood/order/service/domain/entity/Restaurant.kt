package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.RestaurantId
import java.util.UUID

/**
 * This is a Restaurant with the list of [products] available in it.
 */
class Restaurant private constructor(
    id: RestaurantId,
    val products: List<Product>,
    val active: Boolean = false
) : AggregateRoot<RestaurantId>(id) {

    companion object {

        fun instantiate(id: RestaurantId, products: List<Product>, active: Boolean): Restaurant =
            Restaurant(id, products, active)

        fun create(products: List<Product>) =
            instantiate(RestaurantId(UUID.randomUUID()), products, true)

    }

}