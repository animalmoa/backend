package com.server.animalmoa.adoption.repository

import com.server.animalmoa.adoption.domain.Adoption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AdoptionRepository : JpaRepository<Adoption, Long> {
    fun save(adoption: Adoption): Adoption
}
