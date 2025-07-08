package com.server.animalmoa.crawler.exception

// 2025.07.07
// TODO 제일 최신글부터 스크래핑을 시작하다가 이미 스크래핑한 페이지를 만나면, 스크래핑을 그만둔다.
// 하지만 로컬에서 강제로 스크래핑을 그만두면, 영영 스크래핑하지 못 하는 글이 생긴다.
// 1. 로컬 DB와 배포 DB의 분리
// 2. 로컬에서 스크래핑 작업시 모든 트랜잭션을 롤백하도록
class AlreadySavedPostException(
    message: String = "AlreadySavedPostException",
) : Exception(
        message,
    )
