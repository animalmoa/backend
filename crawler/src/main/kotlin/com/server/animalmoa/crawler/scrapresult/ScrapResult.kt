package com.server.animalmoa.crawler.scrapresult

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.common.BaseTime
import com.server.animalmoa.crawler.ScrapType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class ScrapResult(
    val url: String,
    @Enumerated(EnumType.STRING)
    val source: Source,
    @Enumerated(EnumType.STRING)
    val scrapType: ScrapType,
    val isSuccess: Boolean,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
