package com.croman.kfood.customer.service.dataaccess.customer.adapter

import com.croman.kfood.customer.service.dataaccess.customer.mapper.CustomerDataAccessMapper
import com.croman.kfood.customer.service.dataaccess.customer.repository.CustomerJpaRepository
import com.croman.kfood.customer.service.domain.entity.Customer
import com.croman.kfood.customer.service.domain.mapper.CustomerDataMapper
import com.croman.kfood.customer.service.domain.ports.output.repository.CustomerRepository
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryImpl(
    private val jpaRepository: CustomerJpaRepository,
    private val dataMapper: CustomerDataAccessMapper
) : CustomerRepository {

    override fun saveCustomer(customer: Customer): Customer = with(dataMapper) {
        jpaRepository.save(customer.toEntity())
            .toCustomer()
    }

}