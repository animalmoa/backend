dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
}

// common 모듈의 리소스를 api의 bootJar에 포함시키기
tasks.named<Jar>("bootJar") {
    doFirst {
        val deployDbFile = file("../common/src/main/resources/application-deploy-db.yml")
        if (!deployDbFile.exists()) {
            throw GradleException("❌ application-deploy-db.yml 파일이 존재하지 않습니다!")
        } else {
            println("✅ application-deploy-db.yml 파일 확인 완료")
        }
    }
}
