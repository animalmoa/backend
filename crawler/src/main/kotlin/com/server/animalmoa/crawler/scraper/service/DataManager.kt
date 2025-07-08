package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.common.dto.MakeAdoptionDto

abstract class DataManager {
    // 각 정보를 추출하는 것은 수행하지 않으며 정보의 변형만 수행한다.
    abstract fun processDataAndSave(rawDto: MakeAdoptionDto)
}
