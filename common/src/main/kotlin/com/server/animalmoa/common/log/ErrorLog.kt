package com.server.animalmoa.common.log

import com.server.animalmoa.common.common.BaseTime
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ErrorLog(
    val exception: String,
    val message: String,
    val stackTrace: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
