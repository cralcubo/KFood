package com.croman.kfood.order.service.dataaccess.restaurant.repository

import com.croman.kfood.order.service.dataaccess.restaurant.entity.RestaurantEntity
import com.croman.kfood.order.service.dataaccess.restaurant.entity.RestaurantEntityId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, RestaurantEntityId> {
    fun findByRestaurantIdAndProductIdIn(restaurantId: UUID, productIds: List<UUID>): List<RestaurantEntity>
    fun findByRestaurantId(restaurantId: UUID): List<RestaurantEntity>
}