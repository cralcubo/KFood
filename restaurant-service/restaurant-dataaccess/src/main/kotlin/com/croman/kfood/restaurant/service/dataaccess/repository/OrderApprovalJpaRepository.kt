package com.croman.kfood.restaurant.service.dataaccess.repository

import com.croman.kfood.restaurant.service.dataaccess.entity.OrderApprovalEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrderApprovalJpaRepository : JpaRepository<OrderApprovalEntity, UUID>