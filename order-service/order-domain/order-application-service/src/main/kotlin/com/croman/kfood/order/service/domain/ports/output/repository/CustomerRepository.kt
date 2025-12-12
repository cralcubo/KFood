package com.croman.kfood.order.service.domain.ports.output.repository

import com.croman.kfood.order.service.domain.entity.Customer
import java.util.UUID

interface CustomerRepository {
    fun findCustomer(id: UUID): Customer?

}