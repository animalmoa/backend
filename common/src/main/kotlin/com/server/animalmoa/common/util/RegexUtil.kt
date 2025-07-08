package com.server.animalmoa.common.util

object RegexUtil {
    /**
     * 특정 키워드 바로 뒤에 나오는 "한 단어 또는 자연스러운 덩어리"를 추출
     *
     * ex) rawText: "개월수 2개월 암수구분 여아", keyword: "개월수" → 결과: "2개월"
     */
    fun findFirstWordAfterKeyword(
        rawText: String,
        keyword: String,
    ): String? {
        val pattern = """$keyword\s+([^\s\n]+)""".toRegex()
        return pattern.find(rawText)?.groupValues?.getOrNull(1)
    }

    /**
     * 전달받은 문자열에서 모든 공백(스페이스·탭·개행 등)을 싹 지운 새 문자열을 돌려준다.
     */
    fun removeAllWhitespace(text: String): String = text.replace("\\s+".toRegex(), "") // " \\s+" = 모든 종류의 연속된 공백
}
