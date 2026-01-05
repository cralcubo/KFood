package com.croman.kfood.dataaccess.restaurant.service.domain.ports.output.repository

import com.croman.kfood.entity.OrderApproval

interface OrderApprovalRepository {

    fun save(orderApproval: OrderApproval): OrderApproval

}