plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.sentry.jvm.gradle") version "5.9.0"
}
allprojects {
    group = "com.server.animalmoa"
    version = "0.0.1-SNAPSHOT"

    // Java toolchain 설정 (각 서브모듈에 적용)
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // 공통 의존성 설정

    dependencies {

        runtimeOnly("com.oracle.database.jdbc:ojdbc11")
        implementation("com.oracle.database.security:osdt_cert")
        implementation("com.oracle.database.security:oraclepki")
        implementation("com.oracle.database.security:osdt_core")

        implementation("io.github.microutils:kotlin-logging:3.0.5")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("com.mysql:mysql-connector-j")
        runtimeOnly("com.h2database:h2")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        // Spring Boot나 Hibernate가 내부적으로 Jackson의 Jaxb 모듈(jackson-module-jaxb-annotations)을 쓰면서,
        // XmlElement 등 javax.xml.bind.annotation.* 클래스가 필요해짐
        implementation("javax.xml.bind:jaxb-api:2.3.1")
        runtimeOnly("com.sun.xml.bind:jaxb-impl:2.3.1")

        implementation("io.sentry:sentry-spring-boot-starter-jakarta:7.16.0")
        implementation("io.sentry:sentry-logback:7.16.0")
    }

    // Kotlin 컴파일러 옵션 설정
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }
    tasks.register("prepareKotlinBuildScriptModel") {}
    // 테스트 설정
    tasks.withType<Test> {
        useJUnitPlatform()
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    kotlin {
        jvmToolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
}
