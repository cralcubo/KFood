package com.croman.kfood.restaurant.service.domain.ports.input.message.listener

import com.croman.kfood.restaurant.service.domain.dto.RestaurantApprovalRequest

interface RestaurantApprovalRequestMessageListener {

    fun approveOrder(request: com.croman.kfood.restaurant.service.domain.dto.RestaurantApprovalRequest)

}