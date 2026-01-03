package com.croman.kfood.payment.service.domain.valueobject

import com.croman.kfood.domain.valueobject.BaseId
import java.util.UUID

class PaymentId(id: UUID): BaseId<UUID>(id)