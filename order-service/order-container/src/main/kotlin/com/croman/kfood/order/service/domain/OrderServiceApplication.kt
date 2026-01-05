package com.croman.kfood.order.service.domain

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ConfigurationPropertiesScan(basePackages = ["com.croman.kfood"])
@EnableJpaRepositories(basePackages = ["com.croman.kfood.order.service.dataaccess", "com.croman.kfood.dataaccess"])
@EntityScan(basePackages = ["com.croman.kfood.order.service.dataaccess", "com.croman.kfood.dataaccess"])
@SpringBootApplication(scanBasePackages = ["com.croman.kfood"])
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}