package com.server.animalmoa.common

import java.time.LocalDateTime

open class BaseTime(
    open val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
