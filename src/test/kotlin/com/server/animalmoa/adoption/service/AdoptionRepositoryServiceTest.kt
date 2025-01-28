package com.server.animalmoa.adoption.service

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.repository.AdoptionRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayName("분양글 Repository")
class AdoptionRepositoryServiceTest {
    @Autowired
    private lateinit var adoptionRepository: AdoptionRepository

    @Autowired
    lateinit var adoptionRepositoryService: AdoptionRepositoryService

    @Test
    @DisplayName("분양글 생성, 검색, 삭제")
    fun saveAndDeleteAdoption() {
        // given
        val adoptionDto = MakeAdoptionDto.forTest()
        // when
        val adoption = adoptionRepositoryService.save(adoptionDto)
        println(adoption)
        val adoptionSaved = adoptionRepositoryService.findById(adoption?.id)
        println(adoptionSaved)
        // then
        Assertions.assertThat(adoptionRepositoryService.findById(adoption?.id)).isNotNull

        //  when
        adoption?.let {
            adoptionRepositoryService.delete(it)
        }
        val adoptionDeleted = adoptionRepositoryService.findById(adoption?.id)
        // then
        Assertions.assertThat(adoptionDeleted).isNull()
    }
}
