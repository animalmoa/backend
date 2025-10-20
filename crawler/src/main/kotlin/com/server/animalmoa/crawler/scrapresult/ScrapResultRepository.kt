package com.server.animalmoa.crawler.scrapresult

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScrapResultRepository : JpaRepository<ScrapResult, Long>
