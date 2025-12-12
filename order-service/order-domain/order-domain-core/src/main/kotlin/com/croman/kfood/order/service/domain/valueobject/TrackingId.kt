package com.croman.kfood.order.service.domain.valueobject

import com.croman.kfood.domain.valueobject.BaseId
import java.util.UUID

class TrackingId(value: UUID) : BaseId<UUID>(value)