package br.com.created.connectedbackend.integration.external;

import br.com.created.connectedbackend.infrastructure.storage.S3StorageService;
import io.findify.s3mock.S3Mock;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class S3StorageIntegrationTest {

    private static S3Mock s3Mock;
    private static final String BUCKET_NAME = "test-bucket";
    private static final int PORT = 8001;

    @Autowired
    private S3StorageService storageService;

    @BeforeAll
    static void setupS3Mock() {
        s3Mock = new S3Mock.Builder()
                .withPort(PORT)
                .withInMemoryBackend()
                .build();
        s3Mock.start();
    }

    @AfterAll
    static void teardown() {
        s3Mock.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("aws.s3.endpoint", () -> "http://localhost:" + PORT);
        registry.add("aws.s3.bucket", () -> BUCKET_NAME);
        registry.add("aws.s3.region", () -> "us-east-1");
    }

    @Test
    @DisplayName("Deve fazer upload de arquivo para S3")
    void uploadFileToS3() throws IOException {
        // Preparar arquivo de teste
        String content = "Test audio content";
        MultipartFile file = new MockMultipartFile(
                "audio.mp3",
                "audio.mp3",
                "audio/mpeg",
                content.getBytes()
        );

        // Fazer upload
        String fileUrl = storageService.uploadFile(file, "leads/audios");

        // Validar
        assertNotNull(fileUrl);
        assertTrue(fileUrl.contains(BUCKET_NAME));
        assertTrue(fileUrl.contains("leads/audios"));
        assertTrue(storageService.doesFileExist(fileUrl));
    }

    @Test
    @DisplayName("Deve gerar URL pré-assinada para download")
    void generatePresignedUrl() throws IOException {
        // Upload do arquivo
        MultipartFile file = new MockMultipartFile(
                "audio.mp3",
                "audio.mp3",
                "audio/mpeg",
                "Content".getBytes()
        );
        String fileUrl = storageService.uploadFile(file, "leads/audios");

        // Gerar URL pré-assinada
        String presignedUrl = storageService.generatePresignedUrl(fileUrl, 60);

        // Validar
        assertNotNull(presignedUrl);
        assertTrue(presignedUrl.contains("X-Amz-SignedHeaders"));
        assertTrue(presignedUrl.contains("X-Amz-Signature"));
    }

    @Test
    @DisplayName("Deve deletar arquivo do S3")
    void deleteFileFromS3() throws IOException {
        // Upload do arquivo
        MultipartFile file = new MockMultipartFile(
                "delete-test.mp3",
                "delete-test.mp3",
                "audio/mpeg",
                "Delete test content".getBytes()
        );
        String fileUrl = storageService.uploadFile(file, "leads/audios");

        // Deletar arquivo
        storageService.deleteFile(fileUrl);

        // Validar
        assertFalse(storageService.doesFileExist(fileUrl));
    }

    @Test
    @DisplayName("Deve lidar com erros de upload")
    void handleUploadErrors() {
        // Arquivo muito grande
        MultipartFile largefile = new MockMultipartFile(
                "large.mp3",
                "large.mp3",
                "audio/mpeg",
                new byte[11 * 1024 * 1024] // 11MB
        );

        assertThrows(IllegalArgumentException.class, () ->
                storageService.uploadFile(largefile, "leads/audios")
        );
    }

    @Test
    @DisplayName("Deve validar tipos de arquivo permitidos")
    void validateAllowedFileTypes() {
        // Arquivo com tipo não permitido
        MultipartFile invalidFile = new MockMultipartFile(
                "test.exe",
                "test.exe",
                "application/x-msdownload",
                "Invalid content".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () ->
                storageService.uploadFile(invalidFile, "leads/audios")
        );
    }
}