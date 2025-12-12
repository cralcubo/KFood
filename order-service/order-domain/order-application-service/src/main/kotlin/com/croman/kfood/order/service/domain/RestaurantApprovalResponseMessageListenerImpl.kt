package com.croman.kfood.order.service.domain

import com.croman.kfood.order.service.domain.dto.message.RestaurantApprovalResponse
import com.croman.kfood.order.service.domain.ports.input.message.listener.approval.RestaurantApprovalResponseMessageListener
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class RestaurantApprovalResponseMessageListenerImpl: RestaurantApprovalResponseMessageListener {

    override fun orderApproved(response: RestaurantApprovalResponse) {
        TODO("Not yet implemented")
    }

    override fun orderRejected(response: RestaurantApprovalResponse) {
        TODO("Not yet implemented")
    }
}