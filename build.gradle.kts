plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.jpa") version "1.8.0"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "2.7.10"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "com.server.animalmoa"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.seleniumhq.selenium:selenium-java:4.28.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    /*
    Oracle Cloud
     */
    implementation("com.oracle.oci.sdk:oci-java-sdk:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-objectstorage:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-choices:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-jersey3:3.37.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
