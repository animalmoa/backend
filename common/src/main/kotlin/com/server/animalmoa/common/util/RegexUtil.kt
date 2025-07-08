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

    // input 등록일 : 2025.07.08 23:02:11 ~~~
    // output : 2025.07.08 23:02:11

    fun findWordsAfterKeyword(
        rawText: String,
        keyword: String,
        count: Int,
    ): List<String> {
        val escapedKey = Regex.escape(keyword)
        // keyword 뒤의 공백(0개 이상) 다음, 첫 count개의 \S+ 덩어리
        val regex = """$escapedKey\s*((?:\S+\s+){0,${count - 1}}\S+)""".toRegex()
        val match = regex.find(rawText) ?: return emptyList()
        return match.groupValues[1]
            .trim()
            .split(Regex("\\s+"))
            .take(count)
    }

    /**
     * startKeyword 뒤부터 endKeyword가 나오기 전까지의 텍스트를 추출
     *
     * ex) rawText: "분양동물 강아지 - 진돗개 무료분양합니다.",
     *     startKeyword="분양동물", endKeyword="무료분양"
     * → 결과: "강아지 - 진돗개 "
     *
     * startKeyword, endKeyword 둘 중 하나라도 찾지 못한다면 null 반환
     */
    fun findUntilKeyword(
        rawText: String,
        startKeyword: String,
        endKeyword: String,
    ): String? {
        // (?s) : .이 줄바꿈도 매치하도록, non-greedy로 잡아서 endKeyword 전까지
        val regex = """(?s)${Regex.escape(startKeyword)}\s*(.*?)\s*${Regex.escape(endKeyword)}""".toRegex()
        return regex.find(rawText)?.groupValues?.getOrNull(1)
    }

    /**
     * 전달받은 문자열에서 모든 공백(스페이스·탭·개행 등)을 싹 지운 새 문자열을 돌려준다.
     */
    fun removeAllWhitespace(text: String): String = text.replace("\\s+".toRegex(), "") // " \\s+" = 모든 종류의 연속된 공백
}
