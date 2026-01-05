package com.croman.kfood.payment.service.dataaccess.credithistory.adapter

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataMapper
import com.croman.kfood.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository
import com.croman.kfood.payment.service.domain.entity.CreditHistory
import com.croman.kfood.payment.service.domain.port.output.repository.CreditHistoryRepository
import org.springframework.stereotype.Component

@Component
class CreditHistoryRepositoryImpl(
    private val repository: CreditHistoryJpaRepository,
    private val mapper: CreditHistoryDataMapper
) : CreditHistoryRepository {

    override fun save(creditHistory: CreditHistory) =
        with(mapper) {
            repository.save(creditHistory.toEntity())
                .toCreditHistory()
        }

    override fun findByCustomerId(customerId: CustomerId) =
        with(mapper) {
            repository.findByCustomerId(customerId.value)
                ?.map { it.toCreditHistory() }
        }
}