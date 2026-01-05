package com.croman.kfood.order.service.dataaccess.restaurant.adapter

import com.croman.kfood.dataaccess.repository.RestaurantJpaRepository
import com.croman.kfood.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper
import com.croman.kfood.order.service.domain.ports.output.repository.RestaurantRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestaurantRepositoryImpl(
    val jpaRepository: RestaurantJpaRepository,
    val mapper: RestaurantDataAccessMapper
) : RestaurantRepository {

    override fun findRestaurant(id: UUID) =
        jpaRepository.findByIdOrNull(id)?.let { mapper.restaurantEntityToRestaurant(it) }

}