package com.croman.kfood.order.service.dataaccess.customer.adapter

import com.croman.kfood.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper
import com.croman.kfood.order.service.dataaccess.customer.repository.CustomerJpaRepository
import com.croman.kfood.order.service.domain.entity.Customer
import com.croman.kfood.order.service.domain.ports.output.repository.CustomerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CustomerRepositoryImpl(
    private val jpaRepository: CustomerJpaRepository,
    private val mapper: CustomerDataAccessMapper
) : CustomerRepository {

    override fun findCustomer(id: UUID): Customer? = with(mapper) {
        jpaRepository.findByIdOrNull(id)?.toCustomer()
    }
}