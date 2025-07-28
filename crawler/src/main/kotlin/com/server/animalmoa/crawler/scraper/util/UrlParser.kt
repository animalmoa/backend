package com.server.animalmoa.crawler.scraper.util

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object UrlParser {
    /**
     * 주어진 URL에서 특정 쿼리 파라미터의 값을 추출합니다.
     */
    fun extractQueryParam(
        url: String,
        paramName: String,
    ): String? {
        println(url)
        val httpUrl = url.toHttpUrlOrNull() ?: throw IllegalArgumentException("URL '$url' is not a valid URL")
        return httpUrl.queryParameter(paramName)
    }

    /**
     * URL에서 지정한 path 변수 이름 뒤에 오는 세그먼트를 반환합니다.
     * ex) extractPathVariable("…/freecat/8842", "freecat") -> "8842"
     */
    fun extractPathVariable(
        url: String,
        name: String,
    ): String? {
        val httpUrl =
            url.toHttpUrlOrNull()
                ?: throw IllegalArgumentException("URL '$url' is not valid")
        val segments = httpUrl.pathSegments
        val idx = segments.indexOf(name)
        if (idx == -1 || idx + 1 >= segments.size) {
            throw IllegalArgumentException("Path variable '$name' not found in URL '$url'")
        }
        return segments[idx + 1]
    }
}
