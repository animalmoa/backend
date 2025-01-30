package com.server.animalmoa.adoption.service

import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.repository.AdoptionRepository
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class AdoptionRepositoryService(
    val adoptionRepository: AdoptionRepository,
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

    fun ifExistUpdateElseSave(adoption: Adoption): Adoption {
        adoptionRepository
            .findBy(
                source = adoption.source,
                identifier = adoption.identifier,
            )?.let { existingAdoption ->
                logger.info { "before: $existingAdoption" }
                existingAdoption.update(adoption)
                logger.info { "after: $existingAdoption" }
                return adoptionRepository.save(existingAdoption)
            }
        return adoptionRepository.save(adoption)
    }

    fun findAll(
        pageNumber: Int,
        pageSize: Int,
        sort: String,
    ): Page<Adoption> =
        adoptionRepository.findAll(
            PageRequest.of(pageNumber, pageSize, Sort.by(sort).descending()),
        )

    fun findLatestAdoption(
        source: String,
        species: String,
    ): Adoption? =
        adoptionRepository
            .findLatestAdoption(
                source,
                species,
                pageable = PageRequest.of(0, 1),
            ).firstOrNull()
}
