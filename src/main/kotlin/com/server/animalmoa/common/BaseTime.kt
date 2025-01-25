package com.server.animalmoa.common

import java.time.LocalDateTime

open class BaseTime {
    val createdAt: LocalDateTime = LocalDateTime.now()
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
