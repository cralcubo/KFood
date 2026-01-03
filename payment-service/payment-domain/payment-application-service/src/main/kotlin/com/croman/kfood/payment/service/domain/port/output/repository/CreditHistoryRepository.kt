package com.croman.kfood.payment.service.domain.port.output.repository

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.payment.service.domain.entity.CreditEntry
import com.croman.kfood.payment.service.domain.entity.CreditHistory

interface CreditHistoryRepository {
    fun save(creditHistory: CreditHistory): CreditHistory
    fun findByCustomerId(customerId: CustomerId): List<CreditHistory>?
}