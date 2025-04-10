dependencies {
    implementation(project(":common"))
    implementation("org.seleniumhq.selenium:selenium-java:4.28.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    /*
    Oracle Cloud
     */

    // [A] JAX-RS (javax 기반, 2.1.1)
    implementation("javax.ws.rs:javax.ws.rs-api:2.1.1")

    // [B] Jersey 2.x - OCI SDK 3.38.0와 호환
    implementation("org.glassfish.jersey.core:jersey-client:2.36")
    implementation("org.glassfish.jersey.inject:jersey-hk2:2.36")
    implementation("org.glassfish.jersey.core:jersey-common:2.36")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:2.36")

    // ✅ apache-connector (동일 버전)
    implementation("org.glassfish.jersey.connectors:jersey-apache-connector:2.36")
    // [C] OCI SDK - 최소한으로 유지 (중복 모듈 제거)
    implementation("com.oracle.oci.sdk:oci-java-sdk:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-objectstorage:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-jersey:3.38.0")

    /*
    Capture
     */
    implementation("ru.yandex.qatools.ashot:ashot:1.5.4")
}
