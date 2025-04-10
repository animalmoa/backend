package com.server.animalmoa.crawler.crawler.service

import com.server.animalmoa.crawler.exception.DataParseException
import org.springframework.stereotype.Service

@Service
class CrawlerErrorService {
    // 매 게시글마다 에러가 발생한다면, 이를 Catch하고 Log를 출력하도록 구현.
    // 만약 에러 발생시 게시글 저장을 취소하려면 여기서 가능
    fun catchCrawlError(
        function: () -> Unit,
        logger: mu.KLogger,
    ) {
        try {
            function()
        } catch (e: DataParseException) {
            logger.error { e.printStackTrace() }
        } catch (e: Exception) {
            // IdentifierNotFoundException을 포함함
            logger.error { e.printStackTrace() }
        }
    }
}
