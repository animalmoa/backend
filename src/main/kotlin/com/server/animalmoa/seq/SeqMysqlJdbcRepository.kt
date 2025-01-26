package com.server.animalmoa.seq

import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder

class SeqMysqlJdbcRepository(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : SeqRepository {
    override fun findPostSeqByPostTypeAndSource(
        postType: String,
        source: String,
    ): PostSeq? {
        val sql =
            """
            SELECT *
            FROM post_seq
            WHERE post_type = :postType
            AND source = :source
            """.trimIndent()

        val paramSource = BeanPropertySqlParameterSource(PostSeq::class.java)
        return try {
            namedParameterJdbcTemplate.queryForObject(
                sql,
                paramSource,
                BeanPropertyRowMapper(PostSeq::class.java),
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    override fun updatePostSeq(postSeq: PostSeq) {
        val sql =
            """
            UPDATE post_seq
            SET 
            sequence= :sequence,
            updated_at = :updated_at
            WHERE id =:id
            """.trimIndent()

        val paramSource = BeanPropertySqlParameterSource(PostSeq::class.java)
        namedParameterJdbcTemplate.update(sql, paramSource)
    }

    override fun save(postSeq: PostSeq): PostSeq {
        val sql =
            """
            INSERT INTO post_seq
            (post_type,
            source,
            sequence,
            updated_at,
            created_at)
            values(
            :postType,
            :source,
            :sequence,
            :created_at,
            :updated_at
            )
            """.trimIndent()

        val keyHolder = GeneratedKeyHolder()
        val paramSource = BeanPropertySqlParameterSource(PostSeq::class.java)
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder, arrayOf("id"))
        postSeq.id = keyHolder.key?.toLong()
        return postSeq
    }
}
