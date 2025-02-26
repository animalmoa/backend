package com.server.animalmoa.api.adoption.domain

import com.server.animalmoa.api.adoption.data.MakeAdoptionDto
import com.server.animalmoa.api.common.BaseTime
import com.server.animalmoa.api.common.PostType
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
    var identifier: String,
    var title: String,
    var content: String,
    var thumbnailUrl: String,
    var originalUrl: String,
    var viewCount: Int,
    var breed: String,
    var region: String,
    var age: String,
    @Enumerated(EnumType.STRING)
    var species: Species,
    @Enumerated(EnumType.STRING)
    var gender: Gender,
    @Enumerated(EnumType.STRING)
    var source: Source,
    @Enumerated(EnumType.STRING)
    var adoptionStatus: AdoptionStatus,
    @Enumerated(EnumType.STRING)
    var postType: PostType,
    override var createdAt: LocalDateTime,
) : BaseTime(
        createdAt = createdAt,
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    /*
    모든 내용을 덮어쓰되, 조회수는 제외한다
     */
    fun updateExceptViewCount(adoption: Adoption) {
        update(adoption.copy(viewCount = this.viewCount))
    }

    fun update(adoption: Adoption) {
        identifier = adoption.identifier
        title = adoption.title
        content = adoption.content
        viewCount = adoption.viewCount
        thumbnailUrl = adoption.thumbnailUrl
        originalUrl = adoption.originalUrl
        breed = adoption.breed
        region = adoption.region
        age = adoption.age
        species = adoption.species
        gender = adoption.gender
        source = adoption.source
        adoptionStatus = adoption.adoptionStatus
        postType = adoption.postType
    }

    companion object {
        /*
        TODO createdAt 데이터가 Null로 들어오거나 파싱에 실패해도 언제나 Now로 업데이트 되지 않도록 해야한다.
        Dataparsing또는 date가 잘못될 경우 언제나 최신글로 등록됨
         */
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
                species = Species.fromName(makeAdoptionDto.species),
                gender = Gender.fromName(makeAdoptionDto.gender),
                adoptionStatus = AdoptionStatus.fromName(makeAdoptionDto.adoptionStatus),
                postType = PostType.fromName(makeAdoptionDto.postType),
                breed = makeAdoptionDto.breed ?: "",
                region = makeAdoptionDto.region ?: Region.WIDE.name,
                identifier = makeAdoptionDto.identifier ?: UUID.randomUUID().toString(),
                title = makeAdoptionDto.title ?: "",
                content = makeAdoptionDto.content ?: "",
                thumbnailUrl = makeAdoptionDto.thumbnailUrl ?: "",
                originalUrl = makeAdoptionDto.originalUrl,
                source = makeAdoptionDto.source,
                viewCount = 0,
                age = makeAdoptionDto.age ?: "미정",
                createdAt = createdAt, // 변환된 LocalDateTime 사용
            )
        }
    }
}
