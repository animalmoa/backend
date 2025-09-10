@file:Suppress("ktlint:standard:no-empty-file")
// package com.server.animalmoa.crawler.oracle
//
// import com.oracle.bmc.ConfigFileReader
// import com.oracle.bmc.Region
// import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider
// import com.oracle.bmc.objectstorage.ObjectStorageClient
// import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest
// import com.oracle.bmc.objectstorage.requests.PutObjectRequest
// import com.oracle.bmc.objectstorage.transfer.UploadConfiguration
// import com.oracle.bmc.objectstorage.transfer.UploadManager
// import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest
// import org.springframework.stereotype.Service
// import java.io.ByteArrayInputStream
// import java.net.URLDecoder
// import java.net.URLEncoder
// import java.util.UUID
//
// @Service
// class OciObjectStorageService {
//    // 실제 값으로 변경하세요.
//    private val bucketName = "spet-image" // 버킷 이름
//    private val namespaceName = "axbk1tkifgiq" // 네임스페이스 (OCI 콘솔에서 확인)
//    private val region = Region.AP_CHUNCHEON_1 // 예: "ap-chuncheon-1"
//
//    // OCI 설정 파일(~/.oci/config)을 읽어옵니다.
//    val configPath = System.getProperty("user.home") + "/.oci/config"
//    val config = ConfigFileReader.parse(configPath)
//    val provider = ConfigFileAuthenticationDetailsProvider(config)
//
//    fun getClient() =
//        ObjectStorageClient
//            .builder()
//            .region(region)
//            .build(provider)
//
//    // TODO 만약 이미지 url을 그대로 쓸 수 없고 직접 캡쳐해야하는 사이트가 더 생긴다면, 이 로직을 공용으로 사용할 수 있도록 해야한다. 20250313 현재는 네이버 우마동밖에 없기에 보류
//    fun deleteImageByUrl(url: String): Boolean {
//        // URL 형식: https://objectstorage.<regionId>.oraclecloud.com/n/<namespaceName>/b/<bucketName>/o/<encodedFileName>
//        // 정규식으로 URL에서 /o/ 뒤에 오는 인코딩된 파일 이름 추출
//        val regex = """.*/o/(.*)""".toRegex()
//        val matchResult = regex.find(url)
//        val encodedFileName =
//            matchResult?.groups?.get(1)?.value
//                ?: return false // URL 형식이 올바르지 않은 경우
//
//        // URL 디코딩하여 실제 파일 이름을 구합니다.
//        val fileName = URLDecoder.decode(encodedFileName, "UTF-8")
//
//        // ObjectStorageClient 생성
//        val client = getClient()
//        return try {
//            // DeleteObjectRequest 생성
//            val deleteRequest =
//                DeleteObjectRequest
//                    .builder()
//                    .bucketName(bucketName)
//                    .namespaceName(namespaceName)
//                    .objectName(fileName)
//                    .build()
//            // 삭제 요청 실행
//            client.deleteObject(deleteRequest)
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        } finally {
//            client.close()
//        }
//    }
//
//    /**
//     * UploadManager를 사용하여 Public 버킷에 파일(이미지)을 업로드하고,
//     * 외부에서 접근 가능한 URL을 반환합니다.
//     *
//     * @param fileName 업로드할 객체(파일) 이름 (예: "images/test.png")
//     *                 별도로 지정하지 않으면 UUID를 사용하여 랜덤 이름 생성
//     * @param fileData 업로드할 파일 데이터(ByteArray)
//     * @return 업로드된 객체의 Public URL
//     */
//    fun uploadImageAsByteArray(
//        fileName: String = UUID.randomUUID().toString(),
//        fileData: ByteArray,
//    ): String? {
//        val contentType = "image/png"
//
//        // ObjectStorageClient와 UploadManager 생성 (메서드 호출마다 생성 후 close)
//        val client = getClient()
//        val uploadManager = UploadManager(client, UploadConfiguration.builder().build())
//        val request =
//            PutObjectRequest
//                .builder()
//                .bucketName(bucketName)
//                .namespaceName(namespaceName)
//                .objectName(fileName)
//                .contentType(contentType)
//                .build()
//
//        val inputStream = ByteArrayInputStream(fileData)
//        val contentLength = fileData.size.toLong()
//        val uploadDetails =
//            UploadRequest
//                .builder(inputStream, contentLength)
//                .allowOverwrite(true)
//                .build(request)
//        val response = uploadManager.upload(uploadDetails)
//        println("object storage response: $response")
//        // Public 버킷에 업로드했다면, 객체는 아래 URL로 접근할 수 있습니다.
//        // URL 형식: https://objectstorage.<regionId>.oraclecloud.com/n/<namespaceName>/b/<bucketName>/o/<objectName>
//        val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
//
//        client.close()
//        return "https://objectstorage.${region.regionId}.oraclecloud.com/n/$namespaceName/b/$bucketName/o/$encodedFileName"
//    }
//
//    // 20250225 OCI object storage에 파일을 올리는 테스트 메소드이다.
// //    @PostConstruct
// //    fun init() {
// //        uploadByteArray("testFileName", "image/png", "img".toByteArray())
// //    }
// }
