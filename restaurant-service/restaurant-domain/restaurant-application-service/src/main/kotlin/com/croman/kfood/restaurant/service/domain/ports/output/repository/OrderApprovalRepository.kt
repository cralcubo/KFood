package com.croman.kfood.restaurant.service.domain.ports.output.repository

import com.croman.kfood.restaurant.service.domain.entity.OrderApproval


interface OrderApprovalRepository {

    fun save(orderApproval: OrderApproval): OrderApproval

}