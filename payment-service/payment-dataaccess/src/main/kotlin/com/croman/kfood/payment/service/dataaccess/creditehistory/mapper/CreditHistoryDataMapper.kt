package com.croman.kfood.payment.service.dataaccess.creditehistory.mapper

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.domain.valueobject.Money
import com.croman.kfood.payment.service.dataaccess.creditehistory.entity.CreditHistoryEntity
import com.croman.kfood.payment.service.domain.entity.CreditHistory
import com.croman.kfood.payment.service.domain.valueobject.CreditHistoryId
import org.springframework.stereotype.Component

@Component
class CreditHistoryDataMapper {

    fun CreditHistory.toEntity() =
        CreditHistoryEntity(
            id = id.value,
            customerId = customerId.value,
            amount = amount.amount,
            type = transactionType
        )

    fun CreditHistoryEntity.toCreditHistory() =
        CreditHistory.instantiate(
            id = CreditHistoryId(id),
            customerId = CustomerId(customerId),
            amount = Money(amount),
            transactionType = type
        )
}