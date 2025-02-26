package com.server.animalmoa.api.adoption.controller

import com.server.animalmoa.api.adoption.data.GetAdoptionPreviewDto
import com.server.animalmoa.api.adoption.domain.Adoption
import com.server.animalmoa.api.adoption.domain.Region
import com.server.animalmoa.api.adoption.domain.Species
import com.server.animalmoa.api.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.api.page.PageService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ViewController(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val pageService: PageService,
) {
    @GetMapping("/free-adoption")
    fun mainPage(
        model: Model,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "12") size: Int,
        @RequestParam(value = "species") species: Species?,
        @RequestParam(value = "region") region: Region?,
    ): String {
        val adoptionPages: Page<Adoption> =
            adoptionRepositoryService.findAll(
                pageNumber = page - 1,
                pageSize = size,
                species = species,
                region = region,
                sort = Sort.by("createdAt").descending(),
            )
        model.addAttribute("adoptionPages", adoptionPages.map(GetAdoptionPreviewDto::from))
        model.addAttribute("pagination", pageService.getPageInfo(adoptionPages))
        model.addAttribute("regions", Region.getExceptUnknown())
        model.addAttribute("species", Species.getExceptUnknown())
        model.addAttribute("selectedRegion", region)
        model.addAttribute("selectedSpecies", species)
        return "adoption"
    }
}
