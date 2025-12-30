package com.croman.kfood.order.service.dataaccess.restaurant.mapper

import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.order.service.dataaccess.restaurant.entity.RestaurantEntity
import com.croman.kfood.order.service.dataaccess.restaurant.exception.RestaurantDataAccessException
import com.croman.kfood.order.service.domain.entity.Product
import com.croman.kfood.order.service.domain.entity.Restaurant
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestaurantDataAccessMapper {

    fun Restaurant.toProducts() : List<UUID> =
        products.map { it.id.value }


    fun restaurantEntitiesToRestaurant(entities: List<RestaurantEntity>) : Restaurant {
        val entity = entities.firstOrNull()
            ?: throw RestaurantDataAccessException("Rest Entity Not Found")

        val products = entities.map {
            Product.instantiate(
                id = ProductId(it.productId),
                name = it.productName,
                price = Money(it.productPrice)
            )
        }

        return Restaurant.instantiate(RestaurantId(entity.restaurantId), products, entity.restaurantActive)

    }

}