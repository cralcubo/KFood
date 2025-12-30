package com.croman.kfood.order.service.domain

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun orderDomainService() =
        OrderDomainServiceImpl()

}