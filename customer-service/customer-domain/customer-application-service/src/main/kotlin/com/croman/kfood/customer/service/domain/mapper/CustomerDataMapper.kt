package com.croman.kfood.customer.service.domain.mapper

import com.croman.kfood.customer.service.domain.create.CreateCustomerCommand
import com.croman.kfood.customer.service.domain.create.CreateCustomerResponse
import com.croman.kfood.customer.service.domain.entity.Customer
import com.croman.kfood.domain.valueobject.CustomerId
import java.util.UUID

class CustomerDataMapper {

    fun CreateCustomerCommand.toNewCustomer() =
        Customer(
            id = CustomerId(UUID.randomUUID()),
            username = username,
            firstName = firstName,
            lastName = lastName
        )

    fun Customer.toResponse(message: String) =
        CreateCustomerResponse(
            customerId = id.value,
            message = message
        )
}