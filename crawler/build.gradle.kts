dependencies {
    implementation(project(":common"))
    implementation("org.seleniumhq.selenium:selenium-java:4.28.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    /*
    Oracle Cloud
     */
    implementation("com.oracle.oci.sdk:oci-java-sdk:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-objectstorage:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-choices:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-common-httpclient-jersey:3.38.0")
    implementation("com.oracle.oci.sdk:oci-java-sdk-shaded-full:3.38.0")

    /*
    Capture
     */
    implementation("ru.yandex.qatools.ashot:ashot:1.5.4")
}
