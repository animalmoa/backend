package com.server.animalmoa.adoption.service

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.repository.AdoptionRepository
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class AdoptionRepositoryService(
    val adoptionRepository: AdoptionRepository,
) {
    fun save(makeAdoptionDto: MakeAdoptionDto): Adoption {
        val adoption = Adoption.from(makeAdoptionDto)
        return adoptionRepository.save(adoption)
    }

    fun delete(adoption: Adoption) {
        adoptionRepository.delete(adoption)
    }

    fun findById(id: Long?): Adoption? =
        id?.let {
            adoptionRepository.findById(id).getOrNull()
        }
}
