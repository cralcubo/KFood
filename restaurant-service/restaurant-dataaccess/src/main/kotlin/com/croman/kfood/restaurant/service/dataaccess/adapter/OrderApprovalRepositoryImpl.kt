package com.croman.kfood.restaurant.service.dataaccess.adapter

import com.croman.kfood.restaurant.service.dataaccess.mapper.OrderApprovalDataAccessMapper
import com.croman.kfood.restaurant.service.dataaccess.repository.OrderApprovalJpaRepository
import com.croman.kfood.restaurant.service.domain.entity.OrderApproval
import com.croman.kfood.restaurant.service.domain.ports.output.repository.OrderApprovalRepository
import org.springframework.stereotype.Component

@Component
class OrderApprovalRepositoryImpl(
    private val repository: OrderApprovalJpaRepository,
    private val mapper: OrderApprovalDataAccessMapper
) : OrderApprovalRepository {

    override fun save(orderApproval: OrderApproval): OrderApproval = with(mapper) {
        repository.save(orderApproval.toEntity())
            .toOrderApproval()
    }
}