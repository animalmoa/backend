package com.server.animalmoa.common.adoption.domain

import com.server.animalmoa.common.common.BaseTime
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Entity
@Table(
    name = "adoption",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "identifier"]),
    ],
)
data class Adoption(
    @NotNull
    var identifier: String,
    @NotNull
    var title: String,
    @Lob
    @Column
    @NotNull
    var content: String,
    @Column(length = 4000)
    @NotNull
    var thumbnailUrl: String,
    @Column(length = 4000)
    @NotNull
    var originalUrl: String,
    @NotNull
    var viewCount: Int,
    @NotNull
    var breed: String,
    @NotNull
    var region: String,
    @NotNull
    var age: String,
    @Enumerated(EnumType.STRING)
    @NotNull
    var species: Species,
    @Enumerated(EnumType.STRING)
    @NotNull
    var gender: Gender,
    @Enumerated(EnumType.STRING)
    @NotNull
    var source: Source,
    @Enumerated(EnumType.STRING)
    @NotNull
    var adoptionStatus: AdoptionStatus,
    @Enumerated(EnumType.STRING)
    @NotNull
    var postType: PostType,
    @NotNull
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

    fun isThumbnailExists() = thumbnailUrl != ""

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
        // 실제 DB에 저장되고, 클라에서 보여지는 문구이기도 하다.
        const val NOT_DECIDED_STRING = "알 수 없음"

        fun from(makeAdoptionDto: MakeAdoptionDto): Adoption {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            // TODO createdAt 데이터가 Null로 들어오거나 파싱에 실패해도 언제나 Now로 업데이트 되지 않도록 해야한다.
            val createdAt =
                try {
                    makeAdoptionDto.createdAt?.let {
                        LocalDateTime.parse(it, formatter)
                    } ?: LocalDateTime.now() // `createdAt`이 null일 경우 현재 시간
                } catch (e: Exception) {
                    LocalDateTime.now() // 형식이 잘못된 경우 현재 시간
                }

            // 기본 값이다.
            return Adoption(
                species = Species.fromName(makeAdoptionDto.species),
                gender = Gender.fromName(makeAdoptionDto.gender),
                adoptionStatus = AdoptionStatus.fromName(makeAdoptionDto.adoptionStatus),
                postType = PostType.fromName(makeAdoptionDto.postType),
                breed = makeAdoptionDto.breed ?: NOT_DECIDED_STRING,
                region = makeAdoptionDto.region ?: Region.WIDE.name,
                identifier = makeAdoptionDto.identifier ?: UUID.randomUUID().toString(),
                title = makeAdoptionDto.title ?: NOT_DECIDED_STRING,
                content = makeAdoptionDto.content ?: NOT_DECIDED_STRING,
                thumbnailUrl = makeAdoptionDto.thumbnailUrl ?: NOT_DECIDED_STRING,
                originalUrl = makeAdoptionDto.originalUrl,
                source = makeAdoptionDto.source,
                viewCount = 0,
                age = makeAdoptionDto.age ?: NOT_DECIDED_STRING,
                createdAt = createdAt, // 변환된 LocalDateTime 사용
            )
        }
    }
}
