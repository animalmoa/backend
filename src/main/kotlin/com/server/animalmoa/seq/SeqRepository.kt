package com.server.animalmoa.seq

interface SeqRepository {
    fun findPostSeqByPostTypeAndSource(
        postType: String,
        source: String,
    ): PostSeq?

    fun updatePostSeq(postSeq: PostSeq)

    fun save(postSeq: PostSeq): PostSeq
}
