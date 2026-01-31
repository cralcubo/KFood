package com.croman.kfood.restaurant.service.dataaccess.adapter

import com.croman.kfood.dataaccess.repository.RestaurantJpaRepository
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.restaurant.service.dataaccess.mapper.RestaurantDataAccessMapper
import com.croman.kfood.restaurant.service.domain.ports.output.repository.RestaurantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class RestaurantRepositoryImpl(
    private val repository: RestaurantJpaRepository,
    private val mapper: RestaurantDataAccessMapper
) : RestaurantRepository {

    override fun findById(restaurantId: RestaurantId) = with(mapper) {
        repository.findByIdOrNull(restaurantId.value)?.toRestaurant()
    }
}