package com.croman.kfood.order.service.dataaccess.customer.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.order.service.dataaccess.customer.entity.CustomerEntity
import com.croman.kfood.order.service.domain.entity.Customer
import org.springframework.stereotype.Component

@Component
class CustomerDataAccessMapper {

    fun Customer.toEntity()=
        CustomerEntity(id.value)

    fun CustomerEntity.toCustomer() =
        Customer.instantiate(CustomerId(id))

}