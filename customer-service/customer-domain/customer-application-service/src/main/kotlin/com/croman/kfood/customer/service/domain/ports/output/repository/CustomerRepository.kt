package com.croman.kfood.customer.service.domain.ports.output.repository

import com.croman.kfood.customer.service.domain.entity.Customer

interface CustomerRepository {
    fun saveCustomer(customer: Customer): Customer
}