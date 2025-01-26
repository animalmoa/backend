package com.server.animalmoa.seq

import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.common.BaseTime

data class PostSeq(
    var id: Long? = null,
    var postType: String = "",
    var source: String = "",
    var sequence: String = "0",
) : BaseTime() {
    companion object {
        fun standard(): PostSeq =
            PostSeq(
                postType = PostType.FREE_ADOPTION.name,
                source = Source.JUSEYO.name,
                sequence = "0",
            )
    }
}
