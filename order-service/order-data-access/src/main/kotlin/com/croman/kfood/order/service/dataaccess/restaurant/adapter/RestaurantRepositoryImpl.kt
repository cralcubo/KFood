package com.croman.kfood.order.service.dataaccess.restaurant.adapter

import com.croman.kfood.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper
import com.croman.kfood.dataaccess.restaurant.repository.RestaurantJpaRepository
import com.croman.kfood.order.service.domain.entity.Restaurant
import com.croman.kfood.order.service.domain.ports.output.repository.RestaurantRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestaurantRepositoryImpl(
    val jpaRepository: RestaurantJpaRepository,
    val mapper: RestaurantDataAccessMapper
) : RestaurantRepository {

    override fun findRestaurant(id: UUID): Restaurant? {
        val restaurants = jpaRepository.findByRestaurantId(id)
            ?: return null

        return mapper.restaurantEntitiesToRestaurant(restaurants)
    }
}