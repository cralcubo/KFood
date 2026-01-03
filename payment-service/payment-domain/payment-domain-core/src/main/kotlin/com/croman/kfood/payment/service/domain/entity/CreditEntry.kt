package com.croman.kfood.payment.service.domain.entity

import com.croman.kfood.domain.entity.BaseEntity
import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.payment.service.domain.valueobject.CreditEntryId
import java.util.UUID

class CreditEntry private constructor(
    val id: CreditEntryId,
    val customerId: CustomerId,
    val totalCreditAmount: Money
) : BaseEntity<CreditEntryId>(id) {

    companion object {
        fun instantiate(id: CreditEntryId, customerId: CustomerId, totalCreditAmount: Money) =
            CreditEntry(id, customerId, totalCreditAmount)

        fun create(customerId: CustomerId, totalCreditAmount: Money) =
            instantiate(CreditEntryId(UUID.randomUUID()), customerId, totalCreditAmount)
    }

    fun addCredit(amount: Money) =
        modifyCredit(amount, totalCreditAmount::add)

    fun subtractCredit(amount: Money) =
        modifyCredit(amount, totalCreditAmount::subtract)


    private fun modifyCredit(amount: Money, action: (Money) -> Money) =
        CreditEntry(id, customerId, action(amount))
}