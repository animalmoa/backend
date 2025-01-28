package com.server.animalmoa.crawler.service

import com.server.animalmoa.seq.PostSeq

interface PostSequenceService {
    fun updateSequence(updatedSequence: PostSeq)
}
