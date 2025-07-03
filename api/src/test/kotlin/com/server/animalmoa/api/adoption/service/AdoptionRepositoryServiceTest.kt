package com.server.animalmoa.api.adoption.service

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.repository.AdoptionRepositoryService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayName("분양글 Repository")
class AdoptionRepositoryServiceTest {
    @Autowired
    lateinit var adoptionRepositoryService: AdoptionRepositoryService

    @Test
    @DisplayName("분양글 생성, 검색, 삭제")
    fun saveAndDeleteAdoption() {
        // given
        val adoption = Adoption.from(MakeAdoptionDto.forTest())
        // when
        val adoptionFromDb = adoptionRepositoryService.save(adoption)
        println(adoptionFromDb)
        val adoptionSaved = adoptionRepositoryService.findById(adoptionFromDb?.id)
        println(adoptionSaved)
        // then
        Assertions.assertThat(adoptionRepositoryService.findById(adoptionFromDb?.id)).isNotNull

        //  when
        adoptionFromDb?.let {
            adoptionRepositoryService.delete(it)
        }
        val adoptionDeleted = adoptionRepositoryService.findById(adoptionFromDb?.id)
        // then
        Assertions.assertThat(adoptionDeleted).isNull()
    }

    @Test
    @DisplayName("업데이트할 시에 insert가 아닌 update 쿼리")
    fun updateExceptViewCountExceptViewCountAdoption() {
    }
}
