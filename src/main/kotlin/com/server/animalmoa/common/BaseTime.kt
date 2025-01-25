package com.server.animalmoa.common

import java.time.LocalDateTime

open class BaseTime {
    val createAt: LocalDateTime = LocalDateTime.now()
    var updateAt: LocalDateTime = LocalDateTime.now()
}
