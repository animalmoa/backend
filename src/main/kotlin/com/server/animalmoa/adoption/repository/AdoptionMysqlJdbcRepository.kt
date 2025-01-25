package com.server.animalmoa.adoption.repository

import com.server.animalmoa.adoption.domain.Adoption
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository

@Repository
class AdoptionMysqlJdbcRepository(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : AdoptionRepository {
    override fun save(adoption: Adoption): Adoption {
        val sql =
            """
            INSERT INTO adoption (
                species,
                breed,
                gender,
                region,
                adoption_type,
                content,
                thumbnail_url,
                originalUrl,
                source,
                view_count,
                created_at,
                updated_at
            )
            VALUES (
                :species,
                :breed,
                :gender,
                :region,
                :adoptionType,
                :content,
                :thumbnailUrl,
                :originalUrl,
                :source,
                :viewCount,
                :createdAt,
                :updatedAt
            )
            """.trimIndent()
        // 모든 입력 라인의 공통 최소 들여쓰기를 감지하고 라인에서 제거
        // 자동 생성된 PK를 받으려면 KeyHolder 사용
        val keyHolder = GeneratedKeyHolder()

        // BeanPropertySqlParameterSource에 객체를 넣으면,
        // "species" -> adoption.species, "breed" -> adoption.breed 등
        // 필드 이름과 동일한 파라미터를 자동 추출.
        // 리플렉션을 사용(No-args호출 후 Setter (그렇기에 JPA와 달리 var 선언을 해야함)
        val paramSource = BeanPropertySqlParameterSource(adoption)

        // arrayOf("id")로 반환받을
        // mysql 기준 Returning으로 컬럼값을 받을 수 있는데 8.0.19부터 id이외의 것도 가능
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, arrayOf("id"))
        adoption.id = keyHolder.key?.toLong()
        return adoption
    }

    override fun delete(adoption: Adoption) {
        val sql =
            """
            DELETE FROM adoption
            WHERE id = :id
            """.trimIndent()
        val paramSource = BeanPropertySqlParameterSource(adoption)
        namedParameterJdbcTemplate.update(sql, paramSource)
    }

    override fun findById(id: Long): Adoption? {
        val sql =
            """
            SELECT *
            FROM adoption
            WHERE id = :id
            """.trimIndent()
        val paramSource = MapSqlParameterSource("id", id)
        return try {
            namedParameterJdbcTemplate.queryForObject(
                sql,
                paramSource,
                BeanPropertyRowMapper(Adoption::class.java),
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
}
