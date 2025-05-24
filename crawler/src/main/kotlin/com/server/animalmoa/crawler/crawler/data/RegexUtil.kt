package com.server.animalmoa.crawler.crawler.data

object RegexUtil {
    fun getFirstData(
        str: String,
        regex: Regex,
    ): String? =
        regex
            .find(str)
            ?.groupValues
            ?.get(1)
            ?.trim()
}
