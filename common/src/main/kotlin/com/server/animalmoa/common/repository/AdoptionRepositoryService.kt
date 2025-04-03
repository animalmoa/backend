package com.server.animalmoa.common.repository

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.domain.Region
import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.adoption.domain.Species
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
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

    fun ifExistUpdateElseSaveBySourceAndIdentifier(adoption: Adoption): Adoption {
        adoptionRepository
            .findBy(
                source = adoption.source,
                identifier = adoption.identifier,
            )?.let { existingAdoption ->
                logger.info { "before: $existingAdoption" }
                existingAdoption.updateExceptViewCount(adoption)
                logger.info { "after: $existingAdoption" }
                return adoptionRepository.save(existingAdoption)
            }
        return adoptionRepository.save(adoption)
    }

    /*
    TODO  동적 쿼리 추가(KDSL, QueryDsl)
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
