package com.server.animalmoa.common.log

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ErrorLogRepository : JpaRepository<ErrorLog, Long>
