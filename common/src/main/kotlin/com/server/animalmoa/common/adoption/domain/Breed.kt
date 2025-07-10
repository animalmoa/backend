@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.server.animalmoa.common.adoption.domain

import com.server.animalmoa.common.adoption.domain.Species.*
import com.server.animalmoa.common.util.RegexUtil

enum class Breed(
    val species: Species,
    val korean: String,
    val synonyms: Set<String>,
) {
    GOLDEN_DOODLE(DOG, "골든두들", setOf("골든두들")),
    GOLDEN_RETRIEVER(DOG, "골든리트리버", setOf("골든리트리버")),
    GREAT_DANE(DOG, "그레이트덴", setOf("그레이트덴")),
    GREAT_PYRENEES(DOG, "그레이트피레니즈", setOf("그레이트피레니즈")),
    GREYHOUND(DOG, "그레이하운드", setOf("그레이하운드")),
    COTON_DE_TULEAR(DOG, "꼬똥드툴레아", setOf("꼬똥드툴레아")),
    NEWFOUNDLAND(DOG, "뉴펀들랜드", setOf("뉴펀들랜드")),
    DACHSHUND(DOG, "닥스훈트", setOf("닥스훈트")),
    DALMATIAN(DOG, "달마시안", setOf("달마시안")),
    DOGO_ARGENTINO(DOG, "도고아르헨티노", setOf("도고아르헨티노")),
    DOBERMAN(DOG, "도베르만", setOf("도베르만")),
    LABRADOR_RETRIEVER(DOG, "라브라도 리트리버", setOf("라브라도리트리버")),
    LHASA_APSO(DOG, "라사압소", setOf("라사압소")),
    LAIKA(DOG, "라이카", setOf("라이카")),
    ROTTWEILER(DOG, "로트와일러", setOf("로트와일러")),
    MALINOIS(DOG, "마리노이즈", setOf("마리노이즈")),
    MASTIFF(DOG, "마스티프", setOf("마스티프")),
    MALTESE(DOG, "말티즈", setOf("말티즈")),
    MALTIPOO(DOG, "말티푸", setOf("말티푸")),
    MORKIE(DOG, "몰키", setOf("몰키")),
    MINIATURE_PINSCHER(DOG, "미니핀", setOf("미니핀")),
    BASENJI(DOG, "바센지", setOf("바센지")),
    BASSET_HOUND(DOG, "바셋하운드", setOf("바셋하운드")),
    WEIMARANER(DOG, "바이마리너", setOf("바이마리너")),
    BERNESE_MOUNTAIN_DOG(DOG, "버니즈 마운틴독", setOf("버니즈마운틴독")),
    BEDLINGTON_TERRIER(DOG, "베들링턴 테리어", setOf("베들링턴테리어")),
    BORDER_COLLIE(DOG, "보더콜리", setOf("보더콜리")),
    BOSTON_TERRIER(DOG, "보스턴테리어", setOf("보스턴테리어")),
    BOXER(DOG, "복서", setOf("복서")),
    BORZOI(DOG, "볼조이", setOf("볼조이")),
    BULGAE(DOG, "불개", setOf("불개")),
    BULLDOG(DOG, "불독", setOf("불독")),
    BULL_TERRIER(DOG, "불테리어", setOf("불테리어")),
    BRUSSELS_GRIFFON(DOG, "브뤼셀그리폰", setOf("브뤼셀그리폰")),
    BRITTANY(DOG, "브리타니", setOf("브리타니")),
    BEAGLE(DOG, "비글", setOf("비글")),
    BICHON_FRISE(DOG, "비숑 프리제", setOf("비숑프리제")),
    BEARDED_COLLIE(DOG, "비어디드 콜리", setOf("비어디드콜리")),
    VIZSLA(DOG, "비즐라", setOf("비즐라")),
    PAPILLON(DOG, "빠삐용", setOf("빠삐용")),
    SAMOYED(DOG, "사모예드", setOf("사모예드")),
    SAPSAREE(DOG, "삽살이", setOf("삽살이")),
    SHAR_PEI(DOG, "샤페이", setOf("샤페이")),
    SAINT_BERNARD(DOG, "세인트 버나드", setOf("세인트버나드")),
    SHEPHERD(DOG, "세퍼트", setOf("세퍼트")),
    SHETLAND_SHEEPDOG(DOG, "셔틀랜드쉽독", setOf("셔틀랜드쉽독")),
    SCHNAUZER(DOG, "슈나우저", setOf("슈나우저")),
    STANDARD_POODLE(DOG, "스탠다드 푸들", setOf("스탠다드푸들")),
    SHIBA_INU(DOG, "시바견", setOf("시바견")),
    SIBERIAN_HUSKY(DOG, "시베리안 허스키", setOf("시베리안허스키")),
    SHIH_TZU(DOG, "시추", setOf("시추")),
    AMERICAN_COCKER_SPANIEL(DOG, "아메리카 코커 스파니엘", setOf("아메리카코커스파니엘")),
    IRISH_SETTER(DOG, "아이리쉬세타", setOf("아이리쉬세타")),
    AKITA(DOG, "아키타", setOf("아키타")),
    AFGHAN_HOUND(DOG, "아프간 하운드", setOf("아프간하운드")),
    ALASKAN_MALAMUTE(DOG, "알래스카 말라뮤트", setOf("알래스카말라뮤트")),
    ALASKAN_KLEE_KAI(DOG, "알래스칸 클리카이", setOf("알래스칸클리카이")),
    AIREDALE_TERRIER(DOG, "에어데일 테리어", setOf("에어데일테리어")),
    OVCHARKA(DOG, "오브차카", setOf("오브차카")),
    OLD_ENGLISH_SHEEPDOG(DOG, "올드 잉글리쉬 쉽독", setOf("올드잉글리쉬쉽독")),
    WIRE_FOX_TERRIER(DOG, "와이어 폭스테리어", setOf("와이어폭스테리어")),
    YORKSHIRE_TERRIER(DOG, "요크셔테리어", setOf("요크셔테리어")),
    WELSH_CORGI_CARDIGAN(DOG, "웰쉬코기 카디건", setOf("웰쉬코기카디건")),
    ITALIAN_GREYHOUND(DOG, "이탈리안 그레이하운드", setOf("이탈리안그레이하운드")),
    ENGLISH_COCKER_SPANIEL(DOG, "잉글리쉬코커스파니엘", setOf("잉글리쉬코커스파니엘")),
    JACK_RUSSELL_TERRIER(DOG, "잭 러셀 테리어", setOf("잭러셀테리어")),
    JAPANESE_SPITZ(DOG, "저패니즈 스피츠", setOf("저패니즈스피츠")),
    JINDO(DOG, "진돗개", setOf("진돗개")),
    CHOW_CHOW(DOG, "차우차우", setOf("차우차우")),
    CHIHUAHUA(DOG, "치와와", setOf("치와와")),
    CHIN(DOG, "친(chin)", setOf("친", "chin")),
    CANE_CORSO(DOG, "케인코르소", setOf("케인코르소")),
    COLLIE(DOG, "콜리", setOf("콜리")),
    KING_CHARLES_SPANIEL(DOG, "킹 찰스 스파니엘", setOf("킹찰스스파니엘")),
    TOY_POODLE(DOG, "토이푸들", setOf("토이푸들")),
    PUG(DOG, "퍼그", setOf("퍼그")),
    PEKINGESE(DOG, "페키니즈", setOf("페키니즈")),
    WELSH_CORGI(DOG, "웰시 코기", setOf("웰시코기")),
    PEMBROKE_WELSH_CORGI(DOG, "펨브록 웰시코기", setOf("펨브록웰시코기")),
    POMERANIAN(DOG, "포메라니안", setOf("포메라니안")),
    POINTER(DOG, "포인터", setOf("포인터")),
    POMSKY(DOG, "퐁스키", setOf("퐁스키")),
    POM_FITZ(DOG, "퐁피츠", setOf("퐁피츠")),
    POODLE(DOG, "푸들", setOf("푸들")),
    PUNG_SAN(DOG, "풍산개", setOf("풍산개")),
    FRENCH_BULLDOG(DOG, "프렌치 불독", setOf("프렌치불독")),
    PIT_BULL_TERRIER(DOG, "핏불 테리어", setOf("핏불테리어")),
    WHITE_TERRIER(DOG, "화이트 테리어", setOf("화이트테리어")),
    MIXED(DOG, "믹스견", setOf("믹스", "믹스견", "진도믹스", "시고르자브종", "푸숑", "폼피츠", "슈바우저", "보스턴테리어", "브리티쉬롱헤어")),

    // //END OF DOG /////////////////////////////////////////////////////////////////////////////////

    // //CAT ///////////////////////////////////////////////////////////////////////////////////////////////

    NORWEGIAN_FOREST(CAT, "노르웨이 숲", setOf("노르웨이숲")),
    DEVON_REX(CAT, "데본렉스", setOf("데본렉스")),
    RUSSIAN_BLUE(CAT, "러시안 블루", setOf("러시안블루")),
    RAGDOLL(CAT, "렉돌", setOf("렉돌")),
    MUNCHKIN(CAT, "먼치킨", setOf("먼치킨")),
    MAINE_COON(CAT, "메인쿤", setOf("메인쿤")),
    BENGAL(CAT, "뱅갈", setOf("뱅갈")),
    BRITISH_SHORTHAIR(CAT, "브리티쉬 숏헤어", setOf("브리티쉬숏헤어")),
    SIAMESE(CAT, "샴", setOf("샴")),
    SCOTTISH_FOLD(CAT, "스코티쉬 폴드", setOf("스코티쉬폴드")),
    SPHYNX(CAT, "스핑크스", setOf("스핑크스")),
    SINGAPURA(CAT, "싱가푸라", setOf("싱가푸라")),
    AMERICAN_SHORTHAIR(CAT, "아메리칸 숏헤어", setOf("아메리칸숏헤어")),
    ABYSSINIAN(CAT, "아비시니안", setOf("아비시니안")),
    EXOTIC_SHORTHAIR(CAT, "엑조틱 숏헤어", setOf("엑조틱숏헤어")),
    ORIENTAL_LONGHAIR(CAT, "오리엔탈 롱헤어", setOf("오리엔탈롱헤어")),
    ORIENTAL_SHORTHAIR(CAT, "오리엔탈 숏헤어", setOf("오리엔탈숏헤어")),
    CHINCHILLA(CAT, "친칠라", setOf("친칠라")),
    TURKISH_ANGORA(CAT, "터키쉬 앙고라", setOf("터키쉬앙고라")),
    PERSIAN(CAT, "페르시안", setOf("페르시안")),
    KOREAN_CAT(CAT, "한국 고양이", setOf("한국고양이", "코리안숏헤어")),
    ;

    init {
        // korean 중복 체크
        entries
            .map(Breed::korean)
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .takeIf { it.isNotEmpty() }
            ?.let { error("중복된 korean 값 발견: $it") }

        // synonym 중복 체크
        entries
            .flatMap { it.synonyms }
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys
            .takeIf { it.isNotEmpty() }
            ?.let { error("중복된 synonym 값 발견: $it") }
    }

    companion object {
        // synonyms중 없다면 input 그대로 반환
        fun findFromSynonym(text: String?): String? {
            if (text == null) return null
            return Breed.entries
                .find {
                    it.synonyms.any { syn -> RegexUtil.removeAllWhitespace(text).contains(syn) }
                }?.korean ?: text
        }
    }
}
