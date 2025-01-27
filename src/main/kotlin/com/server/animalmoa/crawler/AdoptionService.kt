package com.server.animalmoa.crawler

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.seq.PostSeq

interface AdoptionService {
    fun saveAdoptionIfNotCrawled(makeAdoptionDto: MakeAdoptionDto): PostSeq?

    fun updateSequence(updatedSequence: PostSeq)
}
