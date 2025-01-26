package com.server.animalmoa.seq

class SeqRepositoryService(
    private val seqRepository: SeqRepository,
) {
    fun findPostSeq(
        postType: String,
        source: String,
    ): PostSeq =
        seqRepository.findPostSeqByPostTypeAndSource(postType, source)
            ?: seqRepository.save(
                PostSeq(
                    postType = postType,
                    source = source,
                    sequence = "0",
                ),
            )

    fun updatePostSeq(postSeq: PostSeq) {
        seqRepository.updatePostSeq(postSeq)
    }
}
