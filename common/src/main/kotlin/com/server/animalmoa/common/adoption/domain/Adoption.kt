package com.server.animalmoa.common.adoption.domain

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Breed
import com.server.animalmoa.common.adoption.enum.Gender
import com.server.animalmoa.common.adoption.enum.Region
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
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
    // Breed Enum에 등록된 종이 아닐 그냥 검색
    var breed: String,
    // 2025.07.10 2년 2개월과 같은 정보가 있음으로 현재는 String
    var age: String,
    // Enum으로 확실히 쓸 수 있는 것들에 대해서만 Enum을 사용할 것
    // Enum일 경우 알 수 없는 경우에 대한 value가 있어야한다. ex.REGION.WIDE
    @Enumerated(EnumType.STRING)
    var region: Region,
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
    // TODO override var 지워도 되는지 확인
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

    override fun toString(): String =
        "Adoption(identifier='$identifier', title='$title', content='$content', thumbnailUrl='$thumbnailUrl', originalUrl='$originalUrl', breed='$breed', region='$region', age='$age', species=$species, gender=$gender, source=$source, adoptionStatus=$adoptionStatus, postType=$postType, createdAt=$createdAt, id=$id, viewCount=$viewCount)"

    companion object {
        // 실제 DB에 저장되고, 클라에서 보여지는 문구이기도 하다.
        const val NOT_DECIDED_STRING = "알 수 없음"

        fun from(makeAdoptionDto: MakeAdoptionDto): Adoption {
            // 기본 값이다.
            return Adoption(
                species = Species.fromSynonym(makeAdoptionDto.species),
                gender = Gender.fromSynonym(makeAdoptionDto.gender),
                breed = Breed.findFromSynonym(makeAdoptionDto.breed) ?: NOT_DECIDED_STRING,
                region = Region.fromSynonym(makeAdoptionDto.region),
                adoptionStatus = makeAdoptionDto.adoptionStatus,
                postType = makeAdoptionDto.postType,
                identifier = makeAdoptionDto.identifier,
                title = makeAdoptionDto.title ?: "${makeAdoptionDto.source.korean} [${makeAdoptionDto.postType.korean}]",
                content = makeAdoptionDto.content ?: NOT_DECIDED_STRING,
                thumbnailUrl = makeAdoptionDto.thumbnailUrl ?: NOT_DECIDED_STRING,
                originalUrl = makeAdoptionDto.originalUrl,
                source = makeAdoptionDto.source,
                age = makeAdoptionDto.age ?: "나이 $NOT_DECIDED_STRING",
                // TODO createdAt 데이터가 Null로 들어오거나 파싱에 실패해도 언제나 Now로 업데이트 되지 않도록 해야한다.
                createdAt = makeAdoptionDto.createdAt ?: LocalDateTime.now(),
            )
        }
    }
}
