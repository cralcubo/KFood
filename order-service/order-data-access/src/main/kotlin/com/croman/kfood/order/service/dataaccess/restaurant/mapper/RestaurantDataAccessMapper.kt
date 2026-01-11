package com.croman.kfood.order.service.dataaccess.restaurant.mapper

import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.dataaccess.exception.RestaurantDataAccessException
import com.croman.kfood.dataaccess.entity.RestaurantEntity
import com.croman.kfood.order.service.domain.entity.Product
import com.croman.kfood.order.service.domain.entity.Restaurant
import org.springframework.stereotype.Component

@Component
class RestaurantDataAccessMapper {

    fun restaurantEntityToRestaurant(restaurantEntity: RestaurantEntity?) : Restaurant {
        if(restaurantEntity == null) {
            throw RestaurantDataAccessException("Restaurant Not Found")
        }

        val products = restaurantEntity.products.map {
            Product.instantiate(
                id = ProductId(it.id),
                price = Money(it.price),
            )
        }

        return Restaurant.instantiate(
            RestaurantId(restaurantEntity.id),
            products,
            restaurantEntity.active
        )

    }

}