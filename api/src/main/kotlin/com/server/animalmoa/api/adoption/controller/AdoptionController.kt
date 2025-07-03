package com.server.animalmoa.api.adoption.controller

import com.server.animalmoa.api.adoption.service.AdoptionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AdoptionController(
    private val adoptionService: AdoptionService,
) {
    @ResponseBody
    @PatchMapping("/free-adoption/{id}")
    fun viewAdoptionPost(
        @PathVariable id: Long,
    ) {
        adoptionService.viewAdoptionPost(id)
    }
}
