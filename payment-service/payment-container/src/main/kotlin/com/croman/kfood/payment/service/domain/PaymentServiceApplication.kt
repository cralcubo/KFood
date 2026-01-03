package com.croman.kfood.payment.service.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ConfigurationPropertiesScan(basePackages = ["com.croman.kfood"])
@EnableJpaRepositories(basePackages = ["com.croman.kfood.payment.service.dataaccess"])
@EntityScan(basePackages = ["com.croman.kfood.payment.service.dataaccess"])
@SpringBootApplication(scanBasePackages = ["com.croman.kfood"])
class PaymentServiceApplication

fun main(args: Array<String>) {
    runApplication<PaymentServiceApplication>(*args)
}