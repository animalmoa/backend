package com.server.animalmoa.api.adoption.controller

import com.server.animalmoa.api.adoption.service.AdoptionService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AdoptionRestController(
    private val adoptionService: AdoptionService,
) {
    @ResponseBody
    @PatchMapping("/adoption/free/{id}")
    fun viewAdoptionPost(
        @PathVariable id: Long,
    ) {
        adoptionService.viewAdoptionPost(id)
    }
}
