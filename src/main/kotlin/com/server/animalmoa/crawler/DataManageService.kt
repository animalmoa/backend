package com.server.animalmoa.crawler

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Adoption

interface DataManageService {
    fun parseDataAndSave(rawDto: MakeAdoptionDto): Adoption?
}
