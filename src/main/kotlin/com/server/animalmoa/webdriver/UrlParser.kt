package com.server.animalmoa.webdriver

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.springframework.stereotype.Service

@Service
class UrlParser {
    /**
     * 주어진 URL에서 특정 쿼리 파라미터의 값을 추출합니다.
     */

    fun extractQueryParam(
        url: String,
        paramName: String,
    ): String? {
        println(url)
        val httpUrl = url.toHttpUrlOrNull() ?: return null
        return httpUrl.queryParameter(paramName)
    }
}
