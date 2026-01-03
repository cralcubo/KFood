package com.croman.kfood.payment.service.dataaccess.creditentry.adapter

import com.croman.kfood.domain.valueobject.CustomerId
import com.croman.kfood.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper
import com.croman.kfood.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository
import com.croman.kfood.payment.service.domain.entity.CreditEntry
import com.croman.kfood.payment.service.domain.port.output.repository.CreditEntryRepository
import org.springframework.stereotype.Component

@Component
class CreditEntryRepositoryImpl(
    private val repository: CreditEntryJpaRepository,
    private val mapper: CreditEntryDataAccessMapper
) : CreditEntryRepository {

    override fun save(creditEntry: CreditEntry) =
        with(mapper) {
            repository.save(creditEntry.toEntity())
                .toCreditEntry()
        }

    override fun findByCustomerId(customerId: CustomerId) =
        with(mapper) {
            repository.findByCustomerId(customerId.value)?.toCreditEntry()
        }
}