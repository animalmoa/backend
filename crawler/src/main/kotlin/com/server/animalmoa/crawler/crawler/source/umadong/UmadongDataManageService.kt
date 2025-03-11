package com.server.animalmoa.crawler.crawler.source.umadong

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.repository.AdoptionRepositoryService
import com.server.animalmoa.crawler.crawler.service.DataManager
import com.server.animalmoa.crawler.webdriver.UrlParser
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Service
class UmadongDataManageService(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val urlParser: UrlParser,
) : DataManager(urlParser) {
    override fun parseDataAndSave(rawDto: MakeAdoptionDto): Adoption? {
        // 0) identifier 추출
        val identifier = extractIdentifier(rawDto.identifier, "articleid")
        // 1) 변환
        val createdAt: LocalDateTime? = parseToLocalDateTime(rawDto.createdAt)
        val newAdoption =
            Adoption.from(
                MakeAdoptionDto(
                    species = rawDto.species,
                    breed = rawDto.breed,
                    region = rawDto.region,
                    gender = rawDto.gender,
                    title = rawDto.title,
                    content = rawDto.content,
                    age = rawDto.age,
                    thumbnailUrl = rawDto.thumbnailUrl,
                    postType = rawDto.postType,
                    adoptionStatus = rawDto.adoptionStatus,
                    originalUrl = rawDto.originalUrl,
                    source = rawDto.source,
                    identifier = identifier,
                    createdAt = createdAt.toString(),
                ),
            )
        println(newAdoption)
        return adoptionRepositoryService.ifExistUpdateElseSaveBySourceAndIdentifier(newAdoption)
    }

    fun parseToLocalDateTime(text: String?): LocalDateTime? =
        text?.let {
            // 예: text = "작성일\n2025.02.01. 08:22)"
            // 줄바꿈 이후의 텍스트를 가져온다
            try {
                val datePart = text.substringAfter("\n").trim()
                // 날짜 포맷터 생성 (패턴: yyyy.MM.dd. HH:mm)
                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm")
                // LocalDateTime으로 파싱
                return LocalDateTime.parse(datePart, formatter)
            } catch (e: DateTimeParseException) {
                null
            }
        }
}
    /*
  2025-02-2
   MakeAdoptionDto(
   species=CAT,
   breed=null,
   region=null,
   gender=null,
   title=[무료 분양중] 임보중인 정말 착한 아기 시바 모찌의 가족을 찾습니다,
   content=* 완전 무료로 분양하는 게시판입니다. (2023년 8월 28일 현재부터 적용)
* 동물관련업 허가번호가 있는 분은 무료분양이더라도 본인이 활동가능한 게시판(가정분양 또는 일반분양 게시판)에 글을 올려주세요. 본 게시판은 허가번호가 없는 분양자만 활동가능합니다. (카페규칙 참고)
빠짐없이 기재해 주세요!

1. 연락처(오픈채팅만 올리면 활동정지) :
010391구909삼
2. 지역명 : 구파발
3. 아이 사진 :,
age=null,
thumbnailUrl=https://cafeptthumb-phinf.pstatic.net/MjAyNTAyMDFfMTU4/MDAxNzM4MzczOTc2ODEy.RTS0TzoT-hdM-oZicSY7ETgw9eX1-UIXT7l84mqK93og.lUkC-e6xkqKT_R7O2jqc9TbNIrguUJHEd-7i2x-D3DIg.JPEG/IMG_7013.jpg?type=w800,
postType=FREE_ADOPTION,
adoptionStatus=ING,
originalUrl=https://m.cafe.naver.com/ArticleRead.nhn?clubid=24387804&articleid=2180115&boardtype=L&menuid=30,
source=UMADONG,
identifier=https://m.cafe.naver.com/ca-fe/web/cafes/24387804/articles/2180115?boardtype=L&menuid=30&tc,
createdAt=작성일
2025.02.01. 08:22)
     */
