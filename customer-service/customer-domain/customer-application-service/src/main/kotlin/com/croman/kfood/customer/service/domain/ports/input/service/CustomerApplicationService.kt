package com.croman.kfood.customer.service.domain.ports.input.service

import com.croman.kfood.customer.service.domain.create.CreateCustomerCommand
import com.croman.kfood.customer.service.domain.create.CreateCustomerResponse
import com.croman.kfood.customer.service.domain.entity.Customer
import jakarta.validation.Valid

interface CustomerApplicationService {

    fun createCustomer(@Valid customerCommand: CreateCustomerCommand): CreateCustomerResponse
}