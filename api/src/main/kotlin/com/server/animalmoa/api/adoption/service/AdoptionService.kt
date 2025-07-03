package com.server.animalmoa.api.adoption.service

import com.server.animalmoa.common.repository.AdoptionRepositoryService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AdoptionService(
    private val adoptionRepositoryService: AdoptionRepositoryService,
) {
    @Transactional
    fun viewAdoptionPost(id: Long) {
        adoptionRepositoryService.findById(id)?.let {
            it.viewCount++
        }
    }
}
