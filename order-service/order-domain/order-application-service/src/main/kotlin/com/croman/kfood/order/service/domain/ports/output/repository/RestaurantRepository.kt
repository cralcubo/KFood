package com.croman.kfood.order.service.domain.ports.output.repository

import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.order.service.domain.entity.Restaurant
import java.util.UUID

interface RestaurantRepository {
    fun findRestaurant(id: RestaurantId): Restaurant?

}