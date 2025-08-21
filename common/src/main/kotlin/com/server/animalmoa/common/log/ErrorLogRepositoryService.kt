package com.server.animalmoa.common.log

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ErrorLogRepositoryService(
    private val errorLogRepository: ErrorLogRepository,
) {
    @Transactional
    fun save(e: Exception) =
        errorLogRepository.save(
            ErrorLog(
                exception = e.javaClass.name,
                message = e.message ?: "no message",
                stackTrace = e.stackTraceToString(),
            ),
        )

    @Transactional
    fun deleteBefore(localDateTime: LocalDateTime) {
        errorLogRepository.deleteBefore(localDateTime)
    }
}
