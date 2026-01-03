package com.croman.kfood.payment.service.domain

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun paymentDomainService() =
        PaymentDomainServiceImpl()
}