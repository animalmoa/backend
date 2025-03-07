package com.server.animalmoa.api.adoption.controller

import com.server.animalmoa.common.repository.AdoptionRepositoryService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AdoptionController(
    private val adoptionRepositoryService: AdoptionRepositoryService,
) {
    @ResponseBody
    @PatchMapping("/free-adoption/{id}")
    fun getAdoption(
        @PathVariable id: Long,
    ) {
        adoptionRepositoryService.findById(id)?.let {
            it.update(
                it.copy(
                    viewCount = it.viewCount + 1,
                ),
            )
            adoptionRepositoryService.save(
                it,
            )
        }
    }
}
