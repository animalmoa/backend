package com.server.animalmoa.common.common

import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

/**
 * CreatedAt은 자동으로 지정되거나, 직접 설정 가능
 * UpdatedAt은 자동으로 지정
 */
@MappedSuperclass
abstract class BaseTime(
    open var createdAt: LocalDateTime = LocalDateTime.now(),
) {
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
