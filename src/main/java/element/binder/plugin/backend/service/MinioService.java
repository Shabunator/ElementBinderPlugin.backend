package element.binder.plugin.backend.service;

import element.binder.plugin.backend.security.AppUserDetails;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static io.minio.http.Method.GET;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    /**
     * Метод для загрузки файла в бакет Minio
     *
     * @param images     файлы
     * @param folderPath путь к папке внутри бакета
     * @return ссылки на сохраненные объекты
     */
    @SneakyThrows
    public List<String> uploadFile(MultipartFile[] images, String folderPath) {
        var bucketName = checkBucket();
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            ByteArrayInputStream bais = new ByteArrayInputStream(image.getBytes());
            String objectName = folderPath + "/" + image.getOriginalFilename();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(bais, bais.available(), -1)
                            .contentType(image.getContentType())
                            .build());
            bais.close();

            String fileUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            fileUrls.add(fileUrl);
        }
        log.info("Файлы сохранены в папку: {}", folderPath);
        return fileUrls;
    }

    @SneakyThrows
    public void deleteFolder(String bucketName, String folderName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(folderName + "/")
                        .recursive(true)
                        .build());

        for (Result<Item> result : results) {
            Item item = result.get();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(item.objectName())
                            .build());
            log.info("Удален объект {} из бакета {}", item.objectName(), bucketName);
        }

        log.info("Папка {} и все её содержимое удалены из бакета {}", folderName, bucketName);
    }

    /**
     * Метод для создания папки в бакете Minio
     *
     * @param bucketName имя бакета
     * @param folderName имя папки
     */
    @SneakyThrows
    public void createFolder(String bucketName, String folderName) {
        String folderObjectName = folderName.endsWith("/") ? folderName : folderName + "/";
        createFolderIfNotExists(bucketName, folderObjectName);
    }

    /**
     * Метод для создания иерархии папок в бакете Minio
     *
     * @param bucketName имя бакета
     * @param folderPath путь к папке (например, "folder1/folder2/folder3")
     */
    public void createFolderHierarchy(String bucketName, String folderPath) {
        String[] folders = folderPath.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (String folder : folders) {
            currentPath.append(folder).append("/");
            createFolderIfNotExists(bucketName, currentPath.toString());
        }
    }

    /**
     * Метод для удаления файла из бакета Minio
     *
     * @param bucketName имя бакета
     * @param fileName имя удаляемого файла
     */
    @SneakyThrows
    public void deleteFile(String bucketName, String fileName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build());
        log.info("Файл {} удален из бакета {}", fileName, bucketName);
    }

    /**
     * Метод переименования папки в бакете Minio
     *
     * @param bucketName имя бакета
     * @param oldFolderName старое имя папки
     * @param newFolderName новое имя папки
     */
    public void renameFolder(String bucketName, String oldFolderName, String newFolderName) {
        try {
            if (!oldFolderName.endsWith("/")) {
                oldFolderName += "/";
            }
            if (!newFolderName.endsWith("/")) {
                newFolderName += "/";
            }

            List<String> objectNames = listObjects(bucketName, oldFolderName);

            // Копируем каждый объект в новую папку и удаляем из старой
            for (String objectName : objectNames) {
                String newObjectName = objectName.replaceFirst(oldFolderName, newFolderName);
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(newObjectName)
                                .source(CopySource.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build())
                                .build());

                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build());
            }
        } catch (Exception e) {
            log.error("Ошибка при переименовании папки в Minio", e);
            throw new RuntimeException("Ошибка при переименовании папки в Minio", e);
        }
    }

    /**
     * Метод для проверки наличия бакета в Minio
     *
     * @return имя бакета
     */
    @SneakyThrows
    public String checkBucket() {
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

    /**
     * Метод для получения списка объектов из бакета Minio
     *
     * @param bucketName имя бакета
     * @param prefix префикс для фильтрации объектов из папки
     * @return список объектов
     */
    private List<String> listObjects(String bucketName, String prefix) {
        List<String> objectNames = new ArrayList<>();
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).prefix(prefix).recursive(true).build());
            for (Result<Item> result : results) {
                objectNames.add(result.get().objectName());
            }
        } catch (Exception e) {
            log.error("Ошибка при получении списка объектов из Minio", e);
            throw new RuntimeException("Ошибка при получении списка объектов из Minio", e);
        }
        return objectNames;
    }

    /**
     * Метод для создания папки в бакете Minio, если она не существует
     *
     * @param bucketName имя бакета
     * @param folderName имя папки
     */
    private void createFolderIfNotExists(String bucketName, String folderName) {
        try {
            // Попробуем получить информацию об объекте
            try {
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(folderName)
                                .build());
            } catch (ErrorResponseException e) {
                if (e.errorResponse().code().equals("NoSuchKey")) {
                    // Объект не существует, создаем его
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(folderName)
                                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                    .build());
                    log.info("Папка {} была создана в бакете {}", folderName, bucketName);
                } else {
                    throw e;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при создании папки {} в бакете {}", folderName, bucketName, e);
            throw new RuntimeException("Ошибка при создании папки в Minio", e);
        }
    }
}