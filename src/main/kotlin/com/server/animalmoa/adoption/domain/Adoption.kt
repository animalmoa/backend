package com.server.animalmoa.adoption.domain

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.common.BaseTime
import com.server.animalmoa.common.PostType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "adoption",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "identifier"]),
    ],
)
data class Adoption(
    var title: String,
    var content: String,
    var thumbnailUrl: String,
    var adoptionType: String,
    var originalUrl: String,
    var viewCount: Int,
    var species: String,
    var breed: String,
    var gender: String,
    var region: String,
    var ageByMonth: String,
    @Enumerated(EnumType.STRING)
    var source: Source,
    @Enumerated(EnumType.STRING)
    var adoptionStatus: AdoptionStatus,
    @Enumerated(EnumType.STRING)
    var postType: PostType,
    var identifier: String,
    override var createdAt: LocalDateTime,
) : BaseTime(
        createdAt = createdAt,
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    companion object {
        fun from(makeAdoptionDto: MakeAdoptionDto): Adoption {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val createdAt =
                try {
                    makeAdoptionDto.createdAt?.let {
                        LocalDateTime.parse(it, formatter)
                    } ?: LocalDateTime.now() // `createdAt`이 null일 경우 현재 시간
                } catch (e: Exception) {
                    LocalDateTime.now() // 형식이 잘못된 경우 현재 시간
                }
            return Adoption(
                species = makeAdoptionDto.species ?: "",
                breed = makeAdoptionDto.breed ?: "",
                gender = makeAdoptionDto.gender ?: Gender.NOT_DECIDED.name,
                region = makeAdoptionDto.region ?: Region.WIDE.name,
                adoptionStatus = makeAdoptionDto.adoptionStatus ?: AdoptionStatus.ING,
                identifier = makeAdoptionDto.identifier ?: UUID.randomUUID().toString(),
                title = makeAdoptionDto.title ?: "",
                content = makeAdoptionDto.content ?: "",
                thumbnailUrl = makeAdoptionDto.thumbnailUrl ?: "",
                adoptionType = makeAdoptionDto.postType.name,
                originalUrl = makeAdoptionDto.originalUrl,
                source = makeAdoptionDto.source,
                viewCount = 0,
                ageByMonth = makeAdoptionDto.age ?: "미정",
                createdAt = createdAt, // 변환된 LocalDateTime 사용
                postType = makeAdoptionDto.postType,
            )
        }
    }
}
