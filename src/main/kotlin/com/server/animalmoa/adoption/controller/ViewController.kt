package com.server.animalmoa.adoption.controller

import com.server.animalmoa.adoption.data.GetAdoptionPreviewDto
import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ViewController(
    private val adoptionRepositoryService: AdoptionRepositoryService,
) {
    @GetMapping("/free-adoption")
    fun mainPage(
        model: Model,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int,
    ): String {
        val adoptions: Page<Adoption> =
            adoptionRepositoryService.findAll(
                page,
                size,
            )
        model.addAttribute("adoptions", adoptions.map(GetAdoptionPreviewDto::from))
        return "adoption"
    }
}
