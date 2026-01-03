package com.croman.kfood.payment.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.payment.service.domain.valueobject.CreditHistoryId
import com.croman.kfood.payment.service.domain.valueobject.TransactionType
import java.util.UUID

class CreditHistory private constructor(
    val id: CreditHistoryId,
    val customerId: CustomerId,
    val amount: Money,
    val transactionType:  TransactionType
) : BaseEntity<CreditHistoryId>(id) {

    companion object {
        fun instantiate(id: CreditHistoryId,
                        customerId: CustomerId,
                        amount: Money,
                        transactionType:  TransactionType) =
            CreditHistory(id, customerId, amount, transactionType)

        fun create(customerId: CustomerId,
                   amount: Money,
                   transactionType:  TransactionType) =
            instantiate(CreditHistoryId(UUID.randomUUID()), customerId, amount, transactionType)

    }

}