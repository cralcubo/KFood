package com.croman.kfood.payment.service.dataaccess.payment.adapter

import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper
import com.croman.kfood.payment.service.dataaccess.payment.repository.PaymentJpaRepository
import com.croman.kfood.payment.service.domain.entity.Payment
import com.croman.kfood.payment.service.domain.port.output.repository.PaymentRepository
import org.springframework.stereotype.Component

@Component
class PaymentRepositoryImpl(
    private val repository: PaymentJpaRepository,
    private val dataAccessMapper: PaymentDataAccessMapper
) : PaymentRepository {

    override fun save(payment: Payment) =
        with(dataAccessMapper) {
            repository.save(payment.toEntity())
                .toPayment()
        }


    override fun findByOrderId(orderId: OrderId) =
        with(dataAccessMapper) {
            repository.findByOrderId(orderId.value)?.toPayment()
        }
}