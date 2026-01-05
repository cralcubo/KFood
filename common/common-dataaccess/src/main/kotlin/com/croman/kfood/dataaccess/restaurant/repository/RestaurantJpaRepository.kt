package com.croman.kfood.dataaccess.restaurant.repository

import com.croman.kfood.dataaccess.restaurant.entity.RestaurantEntity
import com.croman.kfood.dataaccess.restaurant.entity.RestaurantEntityId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, RestaurantEntityId> {

    fun findByRestaurantId(restaurantId: UUID): RestaurantEntity?
}