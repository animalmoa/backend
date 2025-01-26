package com.server.animalmoa.seq

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SeqRepositoryServiceTest {
    @Autowired
    lateinit var seqRepositoryService: SeqRepositoryService
    val postSeq: PostSeq = PostSeq.standard()

    @BeforeEach
    @DisplayName("PostSeq 생성")
    fun savePostSeq() {
        seqRepositoryService.savePostSeq(postSeq)
    }

    @AfterEach
    @DisplayName("PostSeq 삭제")
    fun deletePostSeq() {
        seqRepositoryService.deletePostSeq(postSeq)
    }

    @Test
    @DisplayName("PostType, Source로 찾기")
    fun findPostSeq() {
        // when
        val postSeq =
            seqRepositoryService.findPostSeq(
                postType = postSeq.postType,
                source = postSeq.source,
            )
        // then
        Assertions.assertThat(postSeq).isNotNull
    }

    @Test
    @DisplayName("Post_seq 업데이트 후 변경 사항 확인")
    fun updatePostSeq() {
        // when
        val originalSeq = postSeq.sequence
        val updatedPostSeq =
            postSeq.copy(
                sequence = originalSeq + 1,
            )
        // given
        seqRepositoryService.updatePostSeq(updatedPostSeq)

        // then
        Assertions
            .assertThat(
                seqRepositoryService
                    .findPostSeq(postSeq.postType, postSeq.source)
                    .sequence,
            ).isNotEqualTo(originalSeq)
    }
}
