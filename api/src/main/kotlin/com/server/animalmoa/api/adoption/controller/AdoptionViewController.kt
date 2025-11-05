package com.server.animalmoa.api.adoption.controller

import com.server.animalmoa.api.page.PageService
import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.enum.Region
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import com.server.animalmoa.common.adoption.repository.AdoptionRepositoryService
import com.server.animalmoa.common.dto.GetAdoptionPreviewDto
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

@Controller
class AdoptionViewController(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val pageService: PageService,
) {
    @GetMapping("/", "/free-adoption")
    fun rootRedirect(): String = "redirect:https://jipsaya.com/adoption/free"

    @GetMapping("/adoption/free")
    fun getFreeAdoptions(
        model: Model,
        @RequestParam(value = "personal", defaultValue = false.toString()) personal: Boolean,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "12") size: Int,
        @RequestParam(value = "region", defaultValue = "WIDE") region: Region,
        @RequestParam(value = "species") species: Species?,
        // 실제 클라이언트에서 노출되지 않으며 필터링이며 확인용
        @RequestParam(value = "source") source: List<Source>?,
        request: HttpServletRequest,
    ): String {
        val sources =
            if (!source.isNullOrEmpty()) {
                source
            } else {
                Source.entries.filter { it.isPostPersonal == personal }
            }

        val adoptionPages: Page<Adoption> =
            if (personal) {
                adoptionRepositoryService.findAll(
                    pageNumber = page - 1,
                    pageSize = size,
                    species = if (species == Species.UNKNOWN) null else species,
                    region = region,
                    sort = Sort.by("createdAt").descending(),
                    sources = sources,
                )
            } else {
                adoptionRepositoryService.findAllInterleaved(
                    pageNumber = page - 1,
                    pageSize = size,
                    species = if (species == Species.UNKNOWN) null else species,
                    region = region,
                    sort = Sort.by("createdAt").descending(),
                    sources = sources,
                )
            }

        // 파라미터 없을 때만 색인 생성
        model.addAttribute("noindex", request.queryString != null)

        model.addAttribute("regions", Region.getExceptUnknown())
        model.addAttribute("species", Species.getExceptUnknown())

        model.addAttribute("adoptionPages", adoptionPages.map(GetAdoptionPreviewDto::from))
        model.addAttribute("pagination", pageService.getPageInfo(adoptionPages))

        model.addAttribute("selectedRegion", region)
        model.addAttribute("selectedSpecies", species)
        model.addAttribute("personal", personal)
        return "adoption"
    }

    @GetMapping("/adoption/free/{source}")
    fun getFreeAdoptionForSource(
        model: Model,
        @RequestParam(value = "page", defaultValue = "1") page: Int,
        @RequestParam(value = "size", defaultValue = "12") size: Int,
        @RequestParam(value = "species") species: Species?,
        @RequestParam(value = "region") region: Region?,
        @PathVariable source: String,
    ): String {
        val source =
            Source.entries.find { it.name == source } ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Source not found",
            )

        val adoptionPages: Page<Adoption> =
            adoptionRepositoryService.findAll(
                pageNumber = page - 1,
                pageSize = size,
                species = species,
                region = region,
                sources = listOf(source),
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
