package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.crawler.exception.DataParseException
import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import mu.KLogger
import org.springframework.stereotype.Service

@Service
class ScraperErrorService {
    lateinit var logger: KLogger

    // 페이지 접속이 안 될 경우
    // 하위에서 catchScrawlPostError를 호출하기에,하위에서 날리는 에러에 대해 처리해야한다.
    // 404
    fun catchScrawlPostListError(function: () -> Unit) {
        try {
            function()
//        } catch (e: AlreadySavedPostException) {
//            // 이미 스크래핑 한 글이라면, 에러를 상위로 날린다.
//            logger.error { e.message }
//            throw e
        } catch (e: Exception) {
            // 각 게시글이 아니라 게시글 리스트 페이지에서 무언가 에러가 발생한다면, 각 게시글을 스크랩하기 어렵기에 에러를 무시하지 않는다.
            logger.error { e.printStackTrace() }
            throw e
        }
    }

    // 20250411
    // 매 게시글마다 에러가 발생한다면, 이를 Catch하고 Log를 출력하도록 구현.
    // 각 게시글 스크래핑에서 에러가 발생하여도 스크래핑을 멈추지 않기 위함이다.
    // 만약 에러 발생시 게시글 저장을 취소하려면 여기서 가능. (@Transactional 롤백)
    // 다만 현재는 모든 에러가 발생할만한 것들 수행한 이후에 DB insert를 최후에 수행하고 있음으로 불필요함

    // 예상되는 에러가 있을시 아래에 기재해둘 것
    // 404
    fun catchScrawlPostError(function: () -> Unit) {
        try {
            function()
        } catch (e: DataParseException) {
            logger.error { e.printStackTrace() }
        } catch (e: IdentifierNotFoundException) {
            logger.error { e.printStackTrace() }
//        } catch (e: AlreadySavedPostException) {
//            logger.error { e.message }
//            // 이미 스크래핑 한 글이라면, 에러를 상위로 날린다.
//            throw e
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
            // 알수 없는 에러라면, 상위로 날린다.
            throw e
        }
    }
}
