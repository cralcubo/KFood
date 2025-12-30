package com.croman.kfood.order.service.domain.ports.input.message.listener.approval

import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse

interface RestaurantApprovalResponseMessageListener {
    
    fun orderApproved(response: RestaurantApprovalResponse)
    fun orderRejected(response: RestaurantApprovalResponse)
}