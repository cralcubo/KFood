package com.croman.kfood.payment.service.domain.port.output.repository

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.payment.service.domain.entity.CreditEntry

interface CreditEntryRepository {
    fun save(creditEntry: CreditEntry): CreditEntry
    fun findByCustomerId(customerId: CustomerId): CreditEntry?
}