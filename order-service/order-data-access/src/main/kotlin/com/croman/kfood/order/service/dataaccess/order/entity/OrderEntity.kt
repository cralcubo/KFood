package com.croman.kfood.order.service.dataaccess.order.entity

import com.croman.kfood.domain.valueobject.OrderStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.io.Serializable
import java.math.BigDecimal
import java.util.UUID

@Table(name = "orders")
@Entity
data class OrderEntity(
    @Id
    val id: UUID,
    val customerId: UUID,
    val restaurantId: UUID,
    val trackingId: UUID,
    val price: BigDecimal,
    @Enumerated(EnumType.STRING)
    val orderStatus: OrderStatus,
    val failureMessages: String,

    @OneToOne(mappedBy = "order", cascade = [CascadeType.ALL])
    var address: OrderAddressEntity? = null,

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var items: List<OrderItemEntity>? = null,
)

@Table(name = "order_address")
@Entity
data class OrderAddressEntity(
    @Id
    val id: UUID,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "ORDER_ID")
    val order: OrderEntity,

    val street: String,
    val city: String,
    val postalCode: String,
)

@Table(name = "order_items")
@Entity
@IdClass(OrderItemEntityId::class)
data class OrderItemEntity(
    @Id
    val id: UUID,

    @Id
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "ORDER_ID")
    val order: OrderEntity,

    val productId: UUID,
    val price: BigDecimal,
    val quantity: Int,
    val subTotal: BigDecimal,
)

data class OrderItemEntityId(
    val id: UUID? = null,
    val order: OrderEntity? = null
) : Serializable