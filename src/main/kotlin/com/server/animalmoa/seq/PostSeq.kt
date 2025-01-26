package com.server.animalmoa.seq

import com.server.animalmoa.common.BaseTime

data class PostSeq(
    var id: Long? = null,
    var postType: String,
    var source: String,
    val sequence: String,
) : BaseTime()
