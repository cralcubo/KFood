package com.croman.kfood.payment.service.dataaccess.creditentry.repository

import com.croman.kfood.payment.service.dataaccess.creditentry.entity.CreditEntryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CreditEntryJpaRepository : JpaRepository<CreditEntryEntity, UUID> {
    fun findByCustomerId(customerId: UUID): CreditEntryEntity?
}