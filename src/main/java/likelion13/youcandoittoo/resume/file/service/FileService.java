package likelion13.youcandoittoo.resume.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final S3Client s3Client;

    public String uploadPdf(MultipartFile multipartFile, String dir) {

        String fileName = multipartFile.getOriginalFilename();
        String key = dir + UUID.randomUUID() + "-" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(multipartFile.getContentType())
                .build();

        try (InputStream is = multipartFile.getInputStream()) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(is, multipartFile.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("s3 업로드 실패", e);
        }

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    public void deleteByUrl(String fileUrl) {
        // https://bucket.s3.region.amazonaws.com/resumes/xxx.pdf 에서 key 부분만 추출
        String prefix = "https://" + bucket + ".s3." + region + ".amazonaws.com/";
        if (!fileUrl.startsWith(prefix)) return;
        String key = fileUrl.substring(prefix.length());

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
