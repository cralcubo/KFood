package com.croman.kfood.customer.service.application.rest

import com.croman.kfood.customer.service.domain.create.CreateCustomerCommand
import com.croman.kfood.customer.service.domain.create.CreateCustomerResponse
import com.croman.kfood.customer.service.domain.entity.Customer
import com.croman.kfood.customer.service.domain.ports.input.service.CustomerApplicationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/customers")
class CustomerController(
    private val service: CustomerApplicationService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createCustomer(@RequestBody command: CreateCustomerCommand): ResponseEntity<CreateCustomerResponse> {
        logger.info{ "Creating customer ${command.username}" }
        val response = service.createCustomer(command)
        return ResponseEntity.ok(response)
     }
}