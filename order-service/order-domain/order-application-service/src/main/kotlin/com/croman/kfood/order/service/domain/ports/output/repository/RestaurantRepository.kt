package com.croman.kfood.order.service.domain.ports.output.repository

import com.croman.kfood.order.service.domain.entity.Restaurant
import java.util.UUID

interface RestaurantRepository {
    //???
//    fun findRestaurantInformation(restaurant: Restaurant): Restaurant?
    fun findRestaurant(id: UUID): Restaurant?

}