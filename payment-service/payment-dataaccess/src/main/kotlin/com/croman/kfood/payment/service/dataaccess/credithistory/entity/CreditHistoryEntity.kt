package com.croman.kfood.payment.service.dataaccess.credithistory.entity

import com.croman.kfood.payment.service.domain.valueobject.TransactionType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Table(name = "credit_history", schema = "payment")
@Entity
data class CreditHistoryEntity(
    @Id
    val id: UUID,
    val customerId: UUID,
    val amount: BigDecimal,
    @Enumerated(EnumType.STRING)
    val type: TransactionType
)
