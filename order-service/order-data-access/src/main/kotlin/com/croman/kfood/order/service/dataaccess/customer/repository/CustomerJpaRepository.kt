package com.croman.kfood.order.service.dataaccess.customer.repository

import com.croman.kfood.order.service.dataaccess.customer.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CustomerJpaRepository: JpaRepository<CustomerEntity, UUID> {
}