package com.croman.kfood.dataaccess.repository

import com.croman.kfood.dataaccess.entity.RestaurantEntity
import com.croman.kfood.dataaccess.entity.RestaurantEntityId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, RestaurantEntityId> {

    fun findByRestaurantId(restaurantId: UUID): RestaurantEntity?
}