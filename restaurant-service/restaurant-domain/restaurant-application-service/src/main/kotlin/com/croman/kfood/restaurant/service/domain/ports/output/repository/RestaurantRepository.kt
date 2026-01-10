package com.croman.kfood.restaurant.service.domain.ports.output.repository

import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.restaurant.service.domain.entity.Restaurant
import java.util.UUID

interface RestaurantRepository {

    fun findById(restaurantId: RestaurantId): Restaurant?
}