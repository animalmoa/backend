package com.server.animalmoa.adoption.repository

import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.Source
import org.springframework.data.domain.Pageable
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
        And a.species = :species
        ORDER BY a.createdAt DESC
        """,
    )
    fun findLatestAdoption(
        @Param("source") source: String,
        @Param("species") species: String,
        pageable: Pageable,
    ): List<Adoption>

    @Query(
        """
        SELECT a FROM Adoption a 
        WHERE a.source = :source
        And a.identifier = :identifier
        """,
    )
    fun findBy(
        @Param("source") source: Source,
        @Param("identifier") identifier: String,
    ): Adoption?
}
