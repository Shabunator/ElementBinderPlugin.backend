package element.binder.plugin.backend.service;

import element.binder.plugin.backend.security.AppUserDetails;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @SneakyThrows
    public void uploadFile(MultipartFile[] images) {
        var bucketName = checkBucket();
        for (MultipartFile image : images) {
            ByteArrayInputStream bais = new ByteArrayInputStream(image.getBytes());
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(image.getOriginalFilename())
                            .stream(bais, bais.available(), -1)
                            .contentType(image.getContentType())
                            .build());
            bais.close();
        }
        log.info("Файлы сохранены");
    }

    @SneakyThrows
    private String checkBucket() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentPrincipal instanceof AppUserDetails userDetails) {
            var userName = userDetails.getUsername();
            var bucketExistsArgs = BucketExistsArgs.builder().bucket(userName).build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                var makeBucketArgs = MakeBucketArgs.builder().bucket(userName).build();
                minioClient.makeBucket(makeBucketArgs);
                log.info("Bucket {} was created!", userName);
            }
            return userName;
        }
        return null;
    }
}
