package com.server.animalmoa.common.log

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ErrorLogRepository : JpaRepository<ErrorLog, Long> {
    @Modifying
    @Query("DELETE FROM ErrorLog e WHERE e.createdAt < :localDateTime")
    fun deleteBefore(localDateTime: LocalDateTime)
}
