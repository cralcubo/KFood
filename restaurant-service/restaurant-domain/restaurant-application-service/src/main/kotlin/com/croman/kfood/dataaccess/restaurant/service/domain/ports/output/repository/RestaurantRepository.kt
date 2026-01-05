package com.croman.kfood.dataaccess.restaurant.service.domain.ports.output.repository

import com.croman.kfood.entity.Restaurant
import java.util.UUID

interface RestaurantRepository {

    fun findById(restaurantId: UUID): Restaurant?
}