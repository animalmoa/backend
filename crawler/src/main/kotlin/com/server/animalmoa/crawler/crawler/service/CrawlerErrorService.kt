package com.server.animalmoa.crawler.crawler.service

import com.server.animalmoa.api.exception.DataParseException
import org.springframework.stereotype.Service

@Service
class CrawlerErrorService {
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
