package com.server.animalmoa.oracle

import com.oracle.bmc.ConfigFileReader
import com.oracle.bmc.Region
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorageClient
import com.oracle.bmc.objectstorage.requests.PutObjectRequest
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration
import com.oracle.bmc.objectstorage.transfer.UploadManager
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.net.URLEncoder
import java.util.UUID

@Service
class OciObjectStorageService {
    // 실제 값으로 변경하세요.
    private val bucketName = "spet-image" // 버킷 이름
    private val namespaceName = "axbk1tkifgiq" // 네임스페이스 (OCI 콘솔에서 확인)
    private val region = Region.AP_CHUNCHEON_1 // 예: "ap-chuncheon-1"

    /**
     * UploadManager를 사용하여 Public 버킷에 파일(이미지)을 업로드하고,
     * 외부에서 접근 가능한 URL을 반환합니다.
     *
     * @param fileName 업로드할 객체(파일) 이름 (예: "images/test.png")
     *                 별도로 지정하지 않으면 UUID를 사용하여 랜덤 이름 생성
     * @param fileData 업로드할 파일 데이터(ByteArray)
     * @return 업로드된 객체의 Public URL
     */
    fun uploadByteArray(
        fileName: String = UUID.randomUUID().toString(),
        contentType: String,
        fileData: ByteArray,
    ): String {
        // OCI 설정 파일(~/.oci/config)을 읽어옵니다.
        val configPath = System.getProperty("user.home") + "/.oci/config"
        println(configPath)
        val config = ConfigFileReader.parse(configPath, "DEFAULT")
        val provider = ConfigFileAuthenticationDetailsProvider(config)

        // ObjectStorageClient와 UploadManager 생성 (메서드 호출마다 생성 후 close)
        val client =
            ObjectStorageClient
                .builder()
                .region(region)
                .build(provider)
        val uploadManager = UploadManager(client, UploadConfiguration.builder().build())
        val request =
            PutObjectRequest
                .builder()
                .bucketName(bucketName)
                .namespaceName(namespaceName)
                .objectName(fileName)
                .contentType(contentType)
                .build()
        try {
            val inputStream = ByteArrayInputStream(fileData)
            val contentLength = fileData.size.toLong()
            val uploadDetails =
                UploadRequest.builder(inputStream, contentLength).allowOverwrite(true).build(request)
            val response = uploadManager.upload(uploadDetails)
            println(response)
        } finally {
            client.close()
        }

        // Public 버킷에 업로드했다면, 객체는 아래 URL로 접근할 수 있습니다.
        // URL 형식: https://objectstorage.<regionId>.oraclecloud.com/n/<namespaceName>/b/<bucketName>/o/<objectName>
        val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
        return "https://objectstorage.${region.regionId}.oraclecloud.com/n/$namespaceName/b/$bucketName/o/$encodedFileName"
    }
}
