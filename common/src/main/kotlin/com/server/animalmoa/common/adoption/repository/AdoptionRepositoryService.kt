package com.server.animalmoa.common.adoption.repository

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.enum.Region
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class AdoptionRepositoryService(
    private val adoptionRepository: AdoptionRepository,
) {
    private val logger = KotlinLogging.logger {}

    fun save(adoption: Adoption): Adoption? =
        try {
            adoptionRepository.save(adoption)
        } catch (e: DataIntegrityViolationException) {
            null
        }

    fun delete(adoption: Adoption) {
        adoptionRepository.delete(adoption)
    }

    fun findById(id: Long?): Adoption? =
        id?.let {
            adoptionRepository.findById(id).getOrNull()
        }

    fun findBy(
        source: Source,
        identifier: String,
    ): Adoption? = adoptionRepository.findBy(source, identifier)

    @Transactional
    fun ifNewSaveElseUpdate(adoption: Adoption) {
        val foundAdoption = adoptionRepository.findBy(adoption.source, adoption.identifier)
        if (foundAdoption == null) {
            adoptionRepository.save(adoption)
        } else {
            logger.info { "before: $foundAdoption" }
            foundAdoption.updateExceptViewCount(adoption)
            logger.info { "after: $foundAdoption" }
        }
    }

    fun findAfter(localDateTime: LocalDateTime): List<Adoption> = adoptionRepository.findAfter(localDateTime)

    /*
    TODO  동적 쿼리 추가(KDSL, QueryDsl)
    // PostType에 따른 필터링
     */
    fun findAll(
        pageNumber: Int,
        pageSize: Int,
        species: Species?,
        region: Region?,
        sort: Sort,
    ): Page<Adoption> {
        var spec: Specification<Adoption> =
            Specification.where(null)
        spec =
            spec.and(
                AdoptionSpecification
                    .hasSpecies(species),
            )
        spec =
            spec.and(
                AdoptionSpecification
                    .hasRegion(region),
            )
        return adoptionRepository.findAll(
            spec,
            PageRequest.of(
                pageNumber,
                pageSize,
                sort,
            ),
        )
    }
}
