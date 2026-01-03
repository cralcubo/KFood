package com.croman.kfood.payment.service.dataaccess.creditentry.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.payment.service.dataaccess.creditentry.entity.CreditEntryEntity
import com.croman.kfood.payment.service.domain.entity.CreditEntry
import com.croman.kfood.payment.service.domain.valueobject.CreditEntryId
import org.springframework.stereotype.Component

@Component
class CreditEntryDataAccessMapper {

    fun CreditEntry.toEntity() =
        CreditEntryEntity(
            id = id.value,
            customerId = customerId.value,
            totalCreditAmount = totalCreditAmount.amount
        )

    fun CreditEntryEntity.toCreditEntry() =
        CreditEntry.instantiate(
            id = CreditEntryId(id),
            customerId = CustomerId(customerId),
            totalCreditAmount = Money(totalCreditAmount)
        )
}