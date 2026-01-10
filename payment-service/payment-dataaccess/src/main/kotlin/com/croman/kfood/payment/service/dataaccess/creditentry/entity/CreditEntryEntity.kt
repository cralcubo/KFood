package com.croman.kfood.payment.service.dataaccess.creditentry.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Table(name = "credit_entry", schema = "payment")
@Entity
data class CreditEntryEntity(
    @Id
    val id: UUID,
    val customerId: UUID,
    val totalCreditAmount: BigDecimal
)