package com.server.animalmoa.crawler.common.service

import com.server.animalmoa.seq.PostSeq

interface PostSequenceService {
    fun updateSequence(updatedSequence: PostSeq)
}
