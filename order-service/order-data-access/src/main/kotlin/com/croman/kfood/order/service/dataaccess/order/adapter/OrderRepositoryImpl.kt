package com.croman.kfood.order.service.dataaccess.order.adapter

import com.croman.kfood.order.service.dataaccess.order.mapper.OrderDataAccessMapper
import com.croman.kfood.order.service.dataaccess.order.repository.OrderJpaRepository
import com.croman.kfood.order.service.domain.entity.Order
import com.croman.kfood.order.service.domain.ports.output.repository.OrderRepository
import com.croman.kfood.order.service.domain.valueobject.TrackingId
import org.springframework.stereotype.Component

@Component
class OrderRepositoryImpl(
    private val jpaRepository: OrderJpaRepository,
    private val mapper: OrderDataAccessMapper
) : OrderRepository {

    override fun save(order: Order) =
        with(mapper) {
            jpaRepository.save(order.toEntity())
                .toOrder()
        }

    override fun findByTrackingId(trackingId: TrackingId) =
        with(mapper) {
            jpaRepository.findByTrackingId(trackingId.value)
                ?.toOrder()
        }

}

