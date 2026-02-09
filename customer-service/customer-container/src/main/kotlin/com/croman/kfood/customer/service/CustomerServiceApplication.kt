package com.croman.kfood.customer.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ConfigurationPropertiesScan(basePackages = ["com.croman.kfood"])
@EnableJpaRepositories(basePackages = ["com.croman.kfood.customer.service.dataaccess", "com.croman.kfood.dataaccess"])
@EntityScan(basePackages = ["com.croman.kfood.customer.service.dataaccess", "com.croman.kfood.dataaccess"])
@SpringBootApplication(scanBasePackages = ["com.croman.kfood"])
class CustomerServiceApplication

fun main(args: Array<String>) {
    runApplication<CustomerServiceApplication>(*args)
}
