package com.croman.kfood.payment.service.dataaccess.creditehistory.entity

import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.payment.service.domain.valueobject.TransactionType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Table(name = "credit_history")
@Entity
data class CreditHistoryEntity(
    val id: UUID,
    val customerId: UUID,
    val amount: BigDecimal,
    @Enumerated(EnumType.STRING)
    val type: TransactionType
)
