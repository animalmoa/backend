package com.server.animalmoa.common.adoption.domain

enum class AdoptionStatus(
    val korean: String,
    val color: String,
) {
    COMPLETED("분양 완료", "green"),
    ING("분양중", "orangered"),
    ;

    companion object {
        fun fromName(type: String?): AdoptionStatus =
            type?.let {
                AdoptionStatus.entries.find { it.name.equals(type, ignoreCase = true) } ?: ING
            } ?: ING
    }
}
