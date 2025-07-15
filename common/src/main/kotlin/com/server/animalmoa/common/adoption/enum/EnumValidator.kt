package com.server.animalmoa.common.adoption.enum

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class EnumValidator {
    @PostConstruct
    fun init() {
        Breed.entries
    }
}
