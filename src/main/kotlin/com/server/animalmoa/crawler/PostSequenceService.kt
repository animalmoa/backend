package com.server.animalmoa.crawler

import com.server.animalmoa.seq.PostSeq

interface PostSequenceService {
    fun updateSequence(updatedSequence: PostSeq)
}
