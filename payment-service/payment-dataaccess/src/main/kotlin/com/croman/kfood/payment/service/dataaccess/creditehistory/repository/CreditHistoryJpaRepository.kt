package com.croman.kfood.payment.service.dataaccess.creditehistory.repository

import com.croman.kfood.payment.service.dataaccess.creditehistory.entity.CreditHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CreditHistoryJpaRepository: JpaRepository<CreditHistoryEntity, UUID> {
    fun findByCustomerId(customerId: UUID): List<CreditHistoryEntity>?
}