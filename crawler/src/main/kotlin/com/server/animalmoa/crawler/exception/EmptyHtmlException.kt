package com.server.animalmoa.crawler.exception

// 2025.08.19
// 특정 Url에 접속했을 때 빈 Html이 오는 경우로 현재 주세요닷컴에서만 해당 현상이 발생하고 있다.
// 정확한 원인은 파악하기에 현재 어렵고 당장의 해결책은 WebDriver를 재시동하는 것으로 해결한다.
class EmptyHtmlException(
    postUrl: String,
) : Exception(postUrl)
