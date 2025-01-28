package com.server.animalmoa.adoption.domain

enum class AdoptionStatus(
    val korean: String,
) {
    COMPLETED("분양 완료"),
    ING("분양중"),
    ;

    companion object {
        fun fromName(type: String?): AdoptionStatus =
            type?.let {
                AdoptionStatus.entries.find { it.name == type } ?: ING
            } ?: ING
    }
}
