package com.server.animalmoa.seq

import org.springframework.stereotype.Repository

@Repository
interface SeqRepository {
    fun findByPostTypeAndSource(
        postType: String,
        source: String,
    ): PostSeq?

    fun updatePostSeq(postSeq: PostSeq)

    fun save(postSeq: PostSeq): PostSeq

    fun delete(postSeq: PostSeq)
}
