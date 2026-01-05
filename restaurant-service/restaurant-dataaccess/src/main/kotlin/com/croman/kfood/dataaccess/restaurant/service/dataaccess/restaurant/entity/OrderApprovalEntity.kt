package com.croman.kfood.dataaccess.restaurant.service.dataaccess.restaurant.entity

import com.croman.kfood.domain.valueobject.OrderApprovalStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "order-approval", schema = "restaurant")
data class OrderApprovalEntity(
    @Id
    val id: UUID,
    val restaurantId: UUID,
    val orderId: UUID,
    @Enumerated(EnumType.STRING)
    val orderApprovalStatus: OrderApprovalStatus,
)