package com.croman.kfood.restaurant.service.domain

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun restaurantDomainService() =
        RestaurantDomainServiceImpl()
}