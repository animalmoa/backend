package com.server.animalmoa.crawler.crawler.data

object StringUtil {
    // 특정 Text로 시작하는 줄을, 공백을 기준으로 구분하여 반환하는 메소드
    fun getLine(
        allText: String,
        startString: String,
    ): List<String>? {
        val lines =
            allText
                .lines()
                .map { line -> line.trim() }
                .filter { it.isNotEmpty() }

        val splitedLines =
            lines.map { line ->
                line.split("\\s+".toRegex())
            }
        return splitedLines.find {
            println(it)
            it[0].startsWith(startString)
        }
    }
}
