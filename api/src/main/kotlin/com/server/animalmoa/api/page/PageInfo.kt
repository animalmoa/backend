package com.server.animalmoa.api.page

data class PageInfo(
    val currentPage: Int,
    val prevPages: List<Int>,
    val nextPages: List<Int>,
)
