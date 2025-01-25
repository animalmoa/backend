package com.server.animalmoa.adoption.repository

import com.server.animalmoa.adoption.domain.Adoption

interface AdoptionRepository {
    fun save(adoption: Adoption): Adoption

    fun delete(adoption: Adoption)

    fun findById(id: Long): Adoption?
}
