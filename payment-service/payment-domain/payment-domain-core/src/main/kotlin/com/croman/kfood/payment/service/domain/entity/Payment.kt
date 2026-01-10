package com.croman.kfood.payment.service.domain.entity

import com.croman.kfood.domain.entity.AggregateRoot
import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.domain.valueobject.OrderId
import com.croman.kfood.payment.service.domain.exception.PaymentDomainException
import com.croman.kfood.payment.service.domain.valueobject.PaymentId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

sealed class Payment(
    val id: PaymentId,
    val orderId: OrderId,
    val customerId: CustomerId,
    val price: Money,
    val createdAt: ZonedDateTime,
) : AggregateRoot<PaymentId>(id) {

    class Completed(pending: Pending) : Payment(pending.id, pending.orderId, pending.customerId, pending.price, pending.createdAt) {
        fun cancel() = Cancelled(this)
    }

    class Cancelled(completed: Completed) : Payment(completed.id, completed.orderId, completed.customerId, completed.price, completed.createdAt)

    class Failed(pending: Pending) : Payment(pending.id, pending.orderId, pending.customerId, pending.price, pending.createdAt)

    class Pending private constructor(
        id: PaymentId,
        orderId: OrderId,
        customerId: CustomerId,
        price: Money,
        createdAt: ZonedDateTime,
    ) : Payment(id, orderId, customerId, price, createdAt) {

        companion object {

            fun create(orderId: OrderId, customerId: CustomerId, price: Money) =
                instantiate(
                    PaymentId(UUID.randomUUID()),
                    orderId,
                    customerId,
                    price,
                    ZonedDateTime.now(ZoneId.of("UTC"))
                )

            fun instantiate(
                id: PaymentId,
                orderId: OrderId,
                customerId: CustomerId,
                price: Money,
                createdAt: ZonedDateTime
            ) =
                Pending(id, orderId, customerId, price, createdAt)
        }

        fun validatePayment() {
            if (!price.isGreaterThanZero()) {
                throw PaymentDomainException("Price must be greater than zero.")
            }
        }

        fun complete() = Completed(this)
        fun failed() = Failed(this)

    }
}