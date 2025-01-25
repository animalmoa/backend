CREATE TABLE IF NOT EXISTS adoption (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Primary Key, 자동 증가
                                        species VARCHAR(50) NOT NULL,        -- 동물 종 (예: Cat, Dog)
                                        breed VARCHAR(50) NOT NULL,          -- 품종 (예: Siamese, Retriever)
                                        gender VARCHAR(10) NOT NULL,         -- 성별 (예: Male, Female)
                                        region VARCHAR(50) NOT NULL,         -- 지역 (예: Seoul)
                                        adoption_type VARCHAR(50) NOT NULL,
                                        content TEXT,                        -- 추가 내용 (Nullable)
                                        thumbnail_url VARCHAR(255) NOT NULL, -- 썸네일 이미지 URL
                                        view_count INT DEFAULT 0,            -- 조회수 (기본값: 0)
                                        created_at TIMESTAMP NOT NULL,       -- 생성 시간
                                        updated_at TIMESTAMP NOT NULL
);
DROP TABLE adoption;