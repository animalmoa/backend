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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 프로퍼티 추가시 반드시
// 1.toString 재정의 할 것
// 2.updateExceptViewCount
@Entity
@Table(
    name = "adoption",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["source", "identifier"]),
    ],
)
class Adoption(
    var identifier: String,
    var title: String,
    @Lob
    @Column
    var content: String,
    @Column(length = 4000)
    var thumbnailUrl: String,
    @Column(length = 4000)
    var originalUrl: String,
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
    var viewCount: Int = 0

    /*
    모든 내용을 덮어쓰되, 조회수는 제외한다
     */
    fun updateExceptViewCount(adoption: Adoption) {
        identifier = adoption.identifier
        title = adoption.title
        content = adoption.content
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

    fun isThumbnailExists() = thumbnailUrl != ""

    override fun toString(): String =
        "Adoption(identifier='$identifier', title='$title', content='$content', thumbnailUrl='$thumbnailUrl', originalUrl='$originalUrl', breed='$breed', region='$region', age='$age', species=$species, gender=$gender, source=$source, adoptionStatus=$adoptionStatus, postType=$postType, createdAt=$createdAt, id=$id, viewCount=$viewCount)"

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
                gender = Gender.fromSynonym(makeAdoptionDto.gender),
                breed = Breed.find(makeAdoptionDto.species, makeAdoptionDto.breed) ?: "종 $NOT_DECIDED_STRING",
                adoptionStatus = makeAdoptionDto.adoptionStatus,
                postType = makeAdoptionDto.postType,
                region = makeAdoptionDto.region ?: Region.WIDE.name,
                identifier = makeAdoptionDto.identifier,
                title = makeAdoptionDto.title ?: NOT_DECIDED_STRING,
                content = makeAdoptionDto.content ?: NOT_DECIDED_STRING,
                thumbnailUrl = makeAdoptionDto.thumbnailUrl ?: NOT_DECIDED_STRING,
                originalUrl = makeAdoptionDto.originalUrl,
                source = makeAdoptionDto.source,
                age = makeAdoptionDto.age ?: "나이 $NOT_DECIDED_STRING",
                createdAt = createdAt, // 변환된 LocalDateTime 사용
            )
        }
    }
}
