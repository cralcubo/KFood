package com.croman.kfood.dataaccess.repository

import com.croman.kfood.dataaccess.entity.RestaurantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RestaurantJpaRepository : JpaRepository<RestaurantEntity, UUID>