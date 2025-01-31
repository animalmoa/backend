package com.server.animalmoa.page

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class PageService {
    private val maxPrevPages = 4
    private val maxNextPages = 4

    fun <T> getPageInfo(page: Page<T>): PageInfo {
        val currentPage = page.number + 1 // Spring Data JPA의 페이지는 0부터 시작
        val totalPages = page.totalPages

        // 이전 페이지 계산
        val startPrev = (currentPage - maxPrevPages).coerceAtLeast(1)
        val endPrev = (currentPage - 1).coerceAtLeast(1)
        val prevPages = if (currentPage > 1) (startPrev..endPrev).toList() else emptyList()

        // 다음 페이지 계산
        val startNext = (currentPage + 1).coerceAtMost(totalPages)
        val endNext = (currentPage + maxNextPages).coerceAtMost(totalPages)
        val nextPages = if (currentPage < totalPages) (startNext..endNext).toList() else emptyList()

        return PageInfo(
            currentPage = currentPage,
            prevPages = prevPages,
            nextPages = nextPages,
        )
    }
}
