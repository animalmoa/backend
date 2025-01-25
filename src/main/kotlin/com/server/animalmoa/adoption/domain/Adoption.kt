package com.server.animalmoa.adoption.domain

import com.server.animalmoa.common.BaseTime

class Adoption(
    var id: Long?,
    var species: String,
    var breed: String,
    var gender: String,
    var region: String,
    var content: String?,
    var thumbnailUrl: String,
    var adoptionType: String,
    var viewCount: Int,
) : BaseTime()
