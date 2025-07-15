@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.server.animalmoa.common.adoption.enum

import com.server.animalmoa.common.util.RegexUtil

enum class Breed(
    val species: Species,
    val korean: String,
    val synonyms: Set<String>,
) {
    GOLDEN_DOODLE(Species.DOG, "골든두들", setOf("골든두들")),
    GOLDEN_RETRIEVER(Species.DOG, "골든리트리버", setOf("골든리트리버")),
    GREAT_DANE(Species.DOG, "그레이트덴", setOf("그레이트덴")),
    GREAT_PYRENEES(Species.DOG, "그레이트피레니즈", setOf("그레이트피레니즈")),
    GREYHOUND(Species.DOG, "그레이하운드", setOf("그레이하운드")),
    COTON_DE_TULEAR(Species.DOG, "꼬똥드툴레아", setOf("꼬똥드툴레아")),
    NEWFOUNDLAND(Species.DOG, "뉴펀들랜드", setOf("뉴펀들랜드")),
    DACHSHUND(Species.DOG, "닥스훈트", setOf("닥스훈트")),
    DALMATIAN(Species.DOG, "달마시안", setOf("달마시안")),
    DOGO_ARGENTINO(Species.DOG, "도고아르헨티노", setOf("도고아르헨티노")),
    DOBERMAN(Species.DOG, "도베르만", setOf("도베르만")),
    LABRADOR_RETRIEVER(Species.DOG, "라브라도 리트리버", setOf("라브라도리트리버")),
    LHASA_APSO(Species.DOG, "라사압소", setOf("라사압소")),
    LAIKA(Species.DOG, "라이카", setOf("라이카")),
    ROTTWEILER(Species.DOG, "로트와일러", setOf("로트와일러")),
    MALINOIS(Species.DOG, "마리노이즈", setOf("마리노이즈")),
    MASTIFF(Species.DOG, "마스티프", setOf("마스티프")),
    MALTESE(Species.DOG, "말티즈", setOf("말티즈")),
    MALTIPOO(Species.DOG, "말티푸", setOf("말티푸")),
    MORKIE(Species.DOG, "몰키", setOf("몰키")),
    MINIATURE_PINSCHER(Species.DOG, "미니핀", setOf("미니핀")),
    BASENJI(Species.DOG, "바센지", setOf("바센지")),
    BASSET_HOUND(Species.DOG, "바셋하운드", setOf("바셋하운드")),
    WEIMARANER(Species.DOG, "바이마리너", setOf("바이마리너")),
    BERNESE_MOUNTAIN_DOG(Species.DOG, "버니즈 마운틴독", setOf("버니즈마운틴독")),
    BEDLINGTON_TERRIER(Species.DOG, "베들링턴 테리어", setOf("베들링턴테리어")),
    BORDER_COLLIE(Species.DOG, "보더콜리", setOf("보더콜리")),
    BOSTON_TERRIER(Species.DOG, "보스턴테리어", setOf("보스턴테리어")),
    BOXER(Species.DOG, "복서", setOf("복서")),
    BORZOI(Species.DOG, "볼조이", setOf("볼조이")),
    BULGAE(Species.DOG, "불개", setOf("불개")),
    BULLDOG(Species.DOG, "불독", setOf("불독")),
    BULL_TERRIER(Species.DOG, "불테리어", setOf("불테리어")),
    BRUSSELS_GRIFFON(Species.DOG, "브뤼셀그리폰", setOf("브뤼셀그리폰")),
    BRITTANY(Species.DOG, "브리타니", setOf("브리타니")),
    BEAGLE(Species.DOG, "비글", setOf("비글")),
    BICHON_FRISE(Species.DOG, "비숑 프리제", setOf("비숑프리제")),
    BEARDED_COLLIE(Species.DOG, "비어디드 콜리", setOf("비어디드콜리")),
    VIZSLA(Species.DOG, "비즐라", setOf("비즐라")),
    PAPILLON(Species.DOG, "빠삐용", setOf("빠삐용")),
    SAMOYED(Species.DOG, "사모예드", setOf("사모예드")),
    SAPSAREE(Species.DOG, "삽살이", setOf("삽살이")),
    SHAR_PEI(Species.DOG, "샤페이", setOf("샤페이")),
    SAINT_BERNARD(Species.DOG, "세인트 버나드", setOf("세인트버나드")),
    SHEPHERD(Species.DOG, "세퍼트", setOf("세퍼트")),
    SHETLAND_SHEEPDOG(Species.DOG, "셔틀랜드쉽독", setOf("셔틀랜드쉽독")),
    SCHNAUZER(Species.DOG, "슈나우저", setOf("슈나우저")),
    STANDARD_POODLE(Species.DOG, "스탠다드 푸들", setOf("스탠다드푸들")),
    SHIBA_INU(Species.DOG, "시바견", setOf("시바견")),
    SIBERIAN_HUSKY(Species.DOG, "시베리안 허스키", setOf("시베리안허스키")),
    SHIH_TZU(Species.DOG, "시추", setOf("시추")),
    AMERICAN_COCKER_SPANIEL(Species.DOG, "아메리카 코커 스파니엘", setOf("아메리카코커스파니엘")),
    IRISH_SETTER(Species.DOG, "아이리쉬세타", setOf("아이리쉬세타")),
    AKITA(Species.DOG, "아키타", setOf("아키타")),
    AFGHAN_HOUND(Species.DOG, "아프간 하운드", setOf("아프간하운드")),
    ALASKAN_MALAMUTE(Species.DOG, "알래스카 말라뮤트", setOf("알래스카말라뮤트")),
    ALASKAN_KLEE_KAI(Species.DOG, "알래스칸 클리카이", setOf("알래스칸클리카이")),
    AIREDALE_TERRIER(Species.DOG, "에어데일 테리어", setOf("에어데일테리어")),
    OVCHARKA(Species.DOG, "오브차카", setOf("오브차카")),
    OLD_ENGLISH_SHEEPDOG(Species.DOG, "올드 잉글리쉬 쉽독", setOf("올드잉글리쉬쉽독")),
    WIRE_FOX_TERRIER(Species.DOG, "와이어 폭스테리어", setOf("와이어폭스테리어")),
    YORKSHIRE_TERRIER(Species.DOG, "요크셔테리어", setOf("요크셔테리어")),
    WELSH_CORGI_CARDIGAN(Species.DOG, "웰쉬코기 카디건", setOf("웰쉬코기카디건")),
    ITALIAN_GREYHOUND(Species.DOG, "이탈리안 그레이하운드", setOf("이탈리안그레이하운드")),
    ENGLISH_COCKER_SPANIEL(Species.DOG, "잉글리쉬코커스파니엘", setOf("잉글리쉬코커스파니엘")),
    JACK_RUSSELL_TERRIER(Species.DOG, "잭 러셀 테리어", setOf("잭러셀테리어")),
    JAPANESE_SPITZ(Species.DOG, "저패니즈 스피츠", setOf("저패니즈스피츠")),
    JINDO(Species.DOG, "진돗개", setOf("진돗개")),
    CHOW_CHOW(Species.DOG, "차우차우", setOf("차우차우")),
    CHIHUAHUA(Species.DOG, "치와와", setOf("치와와")),
    CHIN(Species.DOG, "친(chin)", setOf("친", "chin")),
    CANE_CORSO(Species.DOG, "케인코르소", setOf("케인코르소")),
    COLLIE(Species.DOG, "콜리", setOf("콜리")),
    KING_CHARLES_SPANIEL(Species.DOG, "킹 찰스 스파니엘", setOf("킹찰스스파니엘")),
    TOY_POODLE(Species.DOG, "토이푸들", setOf("토이푸들")),
    PUG(Species.DOG, "퍼그", setOf("퍼그")),
    PEKINGESE(Species.DOG, "페키니즈", setOf("페키니즈")),
    WELSH_CORGI(Species.DOG, "웰시 코기", setOf("웰시코기")),
    PEMBROKE_WELSH_CORGI(Species.DOG, "펨브록 웰시코기", setOf("펨브록웰시코기")),
    POMERANIAN(Species.DOG, "포메라니안", setOf("포메라니안")),
    POINTER(Species.DOG, "포인터", setOf("포인터")),
    POMSKY(Species.DOG, "퐁스키", setOf("퐁스키")),
    POM_FITZ(Species.DOG, "퐁피츠", setOf("퐁피츠")),
    POODLE(Species.DOG, "푸들", setOf("푸들")),
    PUNG_SAN(Species.DOG, "풍산개", setOf("풍산개")),
    FRENCH_BULLDOG(Species.DOG, "프렌치 불독", setOf("프렌치불독")),
    PIT_BULL_TERRIER(Species.DOG, "핏불 테리어", setOf("핏불테리어")),
    WHITE_TERRIER(Species.DOG, "화이트 테리어", setOf("화이트테리어")),
    MIXED(Species.DOG, "믹스견", setOf("믹스", "믹스견", "진도믹스", "시고르자브종", "푸숑", "폼피츠", "슈바우저", "브리티쉬롱헤어")),

    // //END OF DOG /////////////////////////////////////////////////////////////////////////////////

    // //CAT ///////////////////////////////////////////////////////////////////////////////////////////////

    NORWEGIAN_FOREST(Species.CAT, "노르웨이 숲", setOf("노르웨이숲")),
    DEVON_REX(Species.CAT, "데본렉스", setOf("데본렉스")),
    RUSSIAN_BLUE(Species.CAT, "러시안 블루", setOf("러시안블루")),
    RAGDOLL(Species.CAT, "렉돌", setOf("렉돌")),
    MUNCHKIN(Species.CAT, "먼치킨", setOf("먼치킨")),
    MAINE_COON(Species.CAT, "메인쿤", setOf("메인쿤")),
    BENGAL(Species.CAT, "뱅갈", setOf("뱅갈")),
    BRITISH_SHORTHAIR(Species.CAT, "브리티쉬 숏헤어", setOf("브리티쉬숏헤어")),
    SIAMESE(Species.CAT, "샴", setOf("샴")),
    SCOTTISH_FOLD(Species.CAT, "스코티쉬 폴드", setOf("스코티쉬폴드")),
    SPHYNX(Species.CAT, "스핑크스", setOf("스핑크스")),
    SINGAPURA(Species.CAT, "싱가푸라", setOf("싱가푸라")),
    AMERICAN_SHORTHAIR(Species.CAT, "아메리칸 숏헤어", setOf("아메리칸숏헤어")),
    ABYSSINIAN(Species.CAT, "아비시니안", setOf("아비시니안")),
    EXOTIC_SHORTHAIR(Species.CAT, "엑조틱 숏헤어", setOf("엑조틱숏헤어")),
    ORIENTAL_LONGHAIR(Species.CAT, "오리엔탈 롱헤어", setOf("오리엔탈롱헤어")),
    ORIENTAL_SHORTHAIR(Species.CAT, "오리엔탈 숏헤어", setOf("오리엔탈숏헤어")),
    CHINCHILLA(Species.CAT, "친칠라", setOf("친칠라")),
    TURKISH_ANGORA(Species.CAT, "터키쉬 앙고라", setOf("터키쉬앙고라")),
    PERSIAN(Species.CAT, "페르시안", setOf("페르시안")),
    KOREAN_CAT(Species.CAT, "한국 고양이", setOf("한국고양이", "코리안숏헤어")),
    ;

    companion object {
        // enum 의 init 은 각 상수가 생성될 때마다 실행되기 때문에
        // Enum 클래스가 다 초기화 된 후 한 번만 실행하는 companion obejct안에서 수행한다.
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
