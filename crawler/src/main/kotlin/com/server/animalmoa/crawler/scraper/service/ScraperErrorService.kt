package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.crawler.exception.DataParseException
import org.springframework.stereotype.Service

@Service
class ScraperErrorService {
    // 20250411
    // 매 게시글마다 에러가 발생한다면, 이를 Catch하고 Log를 출력하도록 구현.
    // function은 매 게시글마다 수행하는 작업이어야한다.
    // 만약 에러 발생시 게시글 저장을 취소하려면 여기서 가능. (@Transactional 롤백)
    // 다만 현재는 모든 에러가 발생할만한 것들 수행한 이후에 DB insert를 최후에 수행하고 있음으로 불필요함
    fun catchScrawlError(
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
