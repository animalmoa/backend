package com.server.animalmoa.seq

import org.springframework.stereotype.Service

@Service
class SeqRepositoryService(
    private val seqRepository: SeqRepository,
) {
    fun findPostSeq(
        postType: String,
        source: String,
    ): PostSeq =
        seqRepository.findByPostTypeAndSource(postType, source)
            ?: seqRepository.save(
                PostSeq.standard(),
            )

    fun updatePostSeq(postSeq: PostSeq) {
        seqRepository.updatePostSeq(postSeq)
    }

    fun savePostSeq(postSeq: PostSeq): PostSeq = seqRepository.save(postSeq)

    fun deletePostSeq(postSeq: PostSeq) = seqRepository.delete(postSeq)
}
