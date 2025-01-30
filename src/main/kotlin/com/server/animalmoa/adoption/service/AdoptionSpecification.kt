package com.server.animalmoa.adoption.service

import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.Region
import com.server.animalmoa.adoption.domain.Species
import org.springframework.data.jpa.domain.Specification

/*
빈으로 등록 가능
*/
object AdoptionSpecification {
    fun hasSpecies(species: Species?): Specification<Adoption>? =
        species?.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<Species>("species"), it) // Enum 비교
            }
        }

    fun hasRegion(region: Region?): Specification<Adoption>? {
        // 전국이거나 컬럼이 들어오지 않는다면
        if (region == null || region == Region.WIDE) {
            return null
        }
        return region.let {
            Specification { root, _, criteriaBuilder ->
                criteriaBuilder.equal(root.get<String>("region"), it.name) // Enum 비교
            }
        }
    }
}
