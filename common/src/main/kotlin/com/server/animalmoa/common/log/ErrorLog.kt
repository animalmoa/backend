package com.server.animalmoa.common.log

import com.server.animalmoa.common.common.BaseTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob

@Entity
class ErrorLog(
    val exception: String,
    @Column(length = 4000)
    val message: String,
    @Lob
    val stackTrace: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
