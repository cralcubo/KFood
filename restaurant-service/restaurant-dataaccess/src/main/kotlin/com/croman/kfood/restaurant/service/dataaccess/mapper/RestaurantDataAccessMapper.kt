package com.croman.kfood.restaurant.service.dataaccess.mapper

import com.croman.kfood.dataaccess.entity.ProductEntity
import com.croman.kfood.dataaccess.entity.RestaurantEntity
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.ProductId
import com.croman.kfood.domain.valueobject.RestaurantId
import com.croman.kfood.restaurant.service.domain.entity.Product
import com.croman.kfood.restaurant.service.domain.entity.Restaurant
import org.springframework.stereotype.Component

@Component
class RestaurantDataAccessMapper {

    fun RestaurantEntity.toRestaurant() = Restaurant.instantiate(
        id = RestaurantId(restaurantId),
        active = restaurantActive,
        products = products.map { it.toProduct() }
    )

    private fun ProductEntity.toProduct() =
        Product.instantiate(
            id = ProductId(id),
            name = name,
            price = Money(price),
            available = available
        )


}