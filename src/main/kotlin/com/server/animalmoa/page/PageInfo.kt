package com.server.animalmoa.page

data class PageInfo(
    val currentPage: Int,
    val prevPages: List<Int>,
    val nextPages: List<Int>,
)
