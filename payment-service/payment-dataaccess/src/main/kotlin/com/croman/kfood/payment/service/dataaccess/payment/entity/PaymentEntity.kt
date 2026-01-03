package com.croman.kfood.payment.service.dataaccess.payment.entity

import com.croman.kfood.domain.valueobject.PaymentStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

@Table(name = "payments")
@Entity
data class PaymentEntity(
    @Id
    val id: UUID,
    val customerId: UUID,
    val orderId: UUID,
    val price: BigDecimal,
    @Enumerated(EnumType.STRING)
    val paymentStatus: PaymentStatus,
    val createdAt: ZonedDateTime,
)