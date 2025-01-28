package com.server.animalmoa.adoption.repository

import com.server.animalmoa.adoption.domain.Adoption
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AdoptionRepository : JpaRepository<Adoption, Long> {
    @Query(
        """
        SELECT a FROM Adoption a 
        WHERE a.source = :source
        ORDER BY a.createdAt DESC
        LIMIT 1
        """,
    )
    fun findLatestAdoption(
        @Param("source") source: String,
    ): Adoption?
}
