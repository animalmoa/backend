package com.server.animalmoa.adoption.controller

import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class AdoptionController(
    private val adoptionRepositoryService: AdoptionRepositoryService,
) {
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
