package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.common.log.ErrorLogRepositoryService
import com.server.animalmoa.crawler.exception.DataParseException
import com.server.animalmoa.crawler.exception.IdentifierOrUrlNotFoundException
import mu.KLogger
import org.springframework.stereotype.Service

@Service
class FindPostErrorService(
    private val errorLogRepositoryService: ErrorLogRepositoryService,
) {
    lateinit var logger: KLogger

    // 최상단에서 에러를 잡아내는 메소드. 그렇기에 여기서 한 번 더 에러를 상위로 보내서는 안 된다.
    fun catchScrawlError(function: () -> Unit) {
        try {
            function()
        } catch (e: Exception) {
            logger.error(e) { e.message }
            errorLogRepositoryService.save(e)
        }
    }

    // TODO 페이지 접속이 안 될 경우
    // 하위에서 catchScrawlPostError를 호출하기에,하위에서 날리는 에러에 대해 처리해야한다.
    // 예상되는 에러가 있을시 아래에 기재해둘 것
    // 404
    fun catchScrawlPostListError(function: () -> Unit) {
        try {
            function()
        } catch (e: Exception) {
            logger.error(e) { e.message }
            errorLogRepositoryService.save(e)
            // 각 게시글이 아니라 게시글 리스트 페이지에서 무언가 에러가 발생한다면, 각 게시글을 스크랩하기 어렵기에 에러를 무시하지 않는다.
            throw e
        }
    }

    // 예상되는 에러가 있을시 아래에 기재해둘 것
    // 404
    fun catchScrawlPostError(function: () -> Unit) {
        try {
            function()
        } catch (e: DataParseException) {
            logger.error(e) { e.message }
            errorLogRepositoryService.save(e)
        } catch (e: IdentifierOrUrlNotFoundException) {
            logger.error(e) { e.message }
            errorLogRepositoryService.save(e)
        } catch (e: Exception) {
            logger.error(e) { e.message }
            errorLogRepositoryService.save(e)
            // 알수 없는 에러라면, 상위로 날린다.
            throw e
        }
    }
}
