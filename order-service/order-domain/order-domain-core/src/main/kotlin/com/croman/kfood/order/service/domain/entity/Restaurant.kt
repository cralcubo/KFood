package com.croman.kfood.order.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.RestaurantId

/**
 * This is a Restaurant with the list of [products] available in it.
 */
class Restaurant(
    id: RestaurantId,
    val products: List<Product>,
    val active: Boolean = false
) : AggregateRoot<RestaurantId>(id)