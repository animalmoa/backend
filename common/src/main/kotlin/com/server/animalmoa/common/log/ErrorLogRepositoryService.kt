package com.server.animalmoa.common.log

import org.springframework.stereotype.Service

@Service
class ErrorLogRepositoryService(
    private val errorLogRepository: ErrorLogRepository,
) {
    fun save(e: Exception) =
        errorLogRepository.save(
            ErrorLog(
                exception = e.javaClass.name,
                message = e.message ?: "no message",
                stackTrace = e.stackTraceToString(),
            ),
        )
}
