package com.croman.kfood.payment.service.dataaccess.payment.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.domain.valueobject.PaymentStatus
import com.croman.kfood.payment.service.dataaccess.payment.entity.PaymentEntity
import com.croman.kfood.payment.service.domain.entity.Payment
import com.croman.kfood.payment.service.domain.valueobject.PaymentId
import org.springframework.stereotype.Component

@Component
class PaymentDataAccessMapper {

    fun Payment.toEntity(): PaymentEntity =
        PaymentEntity(
            id = id.value,
            customerId = customerId.value,
            orderId = orderId.value,
            price = price.amount,
            paymentStatus = this.toStatus(),
            createdAt = createdAt
        )

    fun PaymentEntity.toPayment(): Payment {
        val pending = Payment.Pending.instantiate(
            id = PaymentId(id),
            orderId = OrderId(orderId),
            customerId = CustomerId(customerId),
            price = Money(price),
            createdAt = createdAt
        )
        return when (paymentStatus) {
            PaymentStatus.COMPLETED -> pending.complete()
            PaymentStatus.CANCELLED -> pending.cancel()
            PaymentStatus.FAILED -> pending.failed()
            PaymentStatus.PENDING -> pending
        }
    }

    private fun Payment.toStatus() =
        when(this) {
            is Payment.Cancelled -> PaymentStatus.CANCELLED
            is Payment.Completed -> PaymentStatus.COMPLETED
            is Payment.Failed -> PaymentStatus.FAILED
            is Payment.Pending -> PaymentStatus.PENDING
        }
}