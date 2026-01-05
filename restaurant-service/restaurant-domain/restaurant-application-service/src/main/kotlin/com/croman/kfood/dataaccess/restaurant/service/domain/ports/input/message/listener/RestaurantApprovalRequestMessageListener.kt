package com.croman.kfood.dataaccess.restaurant.service.domain.ports.input.message.listener

import com.croman.kfood.dataaccess.restaurant.service.domain.dto.RestaurantApprovalRequest

interface RestaurantApprovalRequestMessageListener {

    fun approveOrder(request: RestaurantApprovalRequest)

}