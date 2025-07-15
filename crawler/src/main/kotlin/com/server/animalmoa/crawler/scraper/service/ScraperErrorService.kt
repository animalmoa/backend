package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.crawler.exception.AlreadySavedPostException
import com.server.animalmoa.crawler.exception.DataParseException
import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import org.springframework.stereotype.Service

@Service
class ScraperErrorService {
    // 20250411
    // 매 게시글마다 에러가 발생한다면, 이를 Catch하고 Log를 출력하도록 구현.
    // 각 게시글 스크래핑에서 에러가 발생하여도 스크래핑을 멈추지 않기 위함이다.
    // 만약 에러 발생시 게시글 저장을 취소하려면 여기서 가능. (@Transactional 롤백)
    // 다만 현재는 모든 에러가 발생할만한 것들 수행한 이후에 DB insert를 최후에 수행하고 있음으로 불필요함

    // 예상되는 에러가 있을시 아래에 기재해둘 것
    fun catchScrawlEachPostError(
        function: () -> Unit,
        logger: mu.KLogger,
    ) {
        try {
            function()
        } catch (e: DataParseException) {
            logger.error { e.printStackTrace() }
        } catch (e: IdentifierNotFoundException) {
            logger.error { e.printStackTrace() }
        } catch (e: AlreadySavedPostException) {
            // 이미 스크래핑 한 글이라면, 에러를 상위로 날린다.
            throw e
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        }
    }
}
