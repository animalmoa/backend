package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import com.server.animalmoa.crawler.scraper.data.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.data.AdoptionToSave
import com.server.animalmoa.crawler.webdriver.UrlParser

abstract class DataManager(
    private val urlParser: UrlParser,
    private val adoptionSaveManager: AdoptionSaveManager,
) {
    /**
     * 데이터를 파싱하여 Adoption 객체를 생성합니다.
     * 생성된 객체는 AdoptionSaveManager를 통해 저장됩니다.
     */
    fun parseDataAndSave(rawDto: MakeAdoptionDto): Adoption? {
        val adoption = parseData(rawDto)
        return adoption?.let { adoptionSaveManager.saveAdoption(it) }
    }

    /**
     * 데이터를 파싱하여 Adoption 객체를 생성합니다.
     */
    abstract fun parseData(rawDto: MakeAdoptionDto): Adoption?

    /**
     * 데이터를 큐에 추가하여 나중에 처리되도록 합니다.
     */
    fun addToQueue(url: String, rawDto: MakeAdoptionDto, priority: Int = 0) {
        val adoptionToSave = AdoptionToSave(
            url = url,
            makeAdoptionDto = rawDto,
            priority = priority,
            parseFunction = this::parseData
        )
        adoptionSaveManager.addAdoptionToQueue(adoptionToSave)
    }

    protected fun extractIdentifier(
        identifier: String?,
        paramName: String,
    ): String =
        identifier?.let {
            val parsedIdentifier =
                urlParser.extractQueryParam(
                    identifier,
                    paramName,
                ) ?: throw IdentifierNotFoundException()
            parsedIdentifier
        } ?: throw IdentifierNotFoundException()
}
