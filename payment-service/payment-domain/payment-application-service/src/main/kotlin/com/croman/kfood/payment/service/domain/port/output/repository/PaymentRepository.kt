package com.croman.kfood.payment.service.domain.port.output.repository

import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.payment.service.domain.entity.Payment

interface PaymentRepository {
    fun save(payment: Payment) : Payment

    fun findByOrderId(orderId: OrderId): Payment?
}