package com.server.animalmoa.common.adoption.repository

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.enum.Region
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
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

    fun findAll(
        pageNumber: Int,
        pageSize: Int,
        species: Species?,
        region: Region?,
        sources: List<Source>,
        sort: Sort,
    ): Page<Adoption> {
        val spec =
            getAdoptionSpec(species, region, sources)

        return adoptionRepository.findAll(
            spec,
            PageRequest.of(
                pageNumber,
                pageSize,
                sort,
            ),
        )
    }

    fun findAllInterleaved(
        pageNumber: Int,
        pageSize: Int,
        species: Species?,
        region: Region?,
        sources: List<Source>,
        sort: Sort,
    ): Page<Adoption> {
        // 각 Source별로 모든 데이터 조회 (정렬 적용)
        val adoptionsPerSource =
            sources.associateWith { source ->
                adoptionRepository.findAll(
                    getAdoptionSpec(species, region, listOf(source)),
                    Sort.by(sort.toList()), // 정렬 적용
                )
            }

        // Interleave 방식으로 전체 리스트 생성
        val interleavedList = mutableListOf<Adoption>()
        val maxSize = adoptionsPerSource.values.maxOfOrNull { it.size } ?: 0

        for (i in 0 until maxSize) {
            sources.forEach { source ->
                val adoptions = adoptionsPerSource[source] ?: emptyList()
                if (i < adoptions.size) {
                    interleavedList.add(adoptions[i])
                }
            }
        }

        // 페이지네이션 적용
        val startIndex = pageNumber * pageSize
        val endIndex = minOf(startIndex + pageSize, interleavedList.size)

        val pagedContent =
            if (startIndex < interleavedList.size) {
                interleavedList.subList(startIndex, endIndex)
            } else {
                emptyList()
            }

        return PageImpl(
            pagedContent,
            PageRequest.of(pageNumber, pageSize, sort),
            interleavedList.size.toLong(),
        )
    }

    private fun getAdoptionSpec(
        species: Species?,
        region: Region?,
        sources: List<Source>,
    ): Specification<Adoption?> {
        val spec =
            Specification.allOf(
                AdoptionSpecification.hasSpecies(species),
                AdoptionSpecification.hasRegion(region),
                AdoptionSpecification.hasSource(sources),
            )

        return spec
    }
}
