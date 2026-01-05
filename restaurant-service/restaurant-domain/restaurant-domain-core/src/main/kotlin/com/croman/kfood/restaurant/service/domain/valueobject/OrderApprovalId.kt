package com.croman.kfood.restaurant.service.domain.valueobject

import com.croman.kfood.domain.valueobject.BaseId
import java.util.UUID

class OrderApprovalId(value: UUID) : BaseId<UUID>(value)