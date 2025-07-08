package com.server.animalmoa.crawler.scraper.util

import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object UrlParser {
    /**
     * 주어진 URL에서 특정 쿼리 파라미터의 값을 추출합니다.
     */
    fun extractQueryParam(
        url: String,
        paramName: String,
    ): String {
        println(url)
        val httpUrl = url.toHttpUrlOrNull() ?: throw IllegalArgumentException("URL '$url' is not a valid URL")
        return httpUrl.queryParameter(paramName) ?: throw IdentifierNotFoundException()
    }
}
