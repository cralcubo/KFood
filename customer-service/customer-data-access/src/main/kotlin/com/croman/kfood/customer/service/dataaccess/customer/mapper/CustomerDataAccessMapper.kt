package com.croman.kfood.customer.service.dataaccess.customer.mapper

import com.croman.kfood.customer.service.dataaccess.customer.entity.CustomerEntity
import com.croman.kfood.customer.service.domain.entity.Customer
import com.croman.kfood.domain.valueobject.CustomerId

class CustomerDataAccessMapper {
    fun CustomerEntity.toCustomer() = Customer(
        id = CustomerId(id),
        username = username,
        firstName = firstName,
        lastName = lastName
    )

    fun Customer.toEntity() =
        CustomerEntity(
            id = id.value,
            username = username,
            firstName = firstName,
            lastName = lastName
        )
}