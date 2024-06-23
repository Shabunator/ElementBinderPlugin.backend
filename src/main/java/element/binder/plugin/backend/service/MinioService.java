package element.binder.plugin.backend.service;

import element.binder.plugin.backend.security.AppUserDetails;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    /**
     * Метод для загрузки файла в бакет Minio
     *
     * @param images файлы
     */
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
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(folderName + "/")
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .build());
        log.info("Папка {} была создана в бакете {}", folderName, bucketName);
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
            boolean folderExists = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(folderName)
                            .build()) != null;

            if (!folderExists) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(folderName)
                                .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                                .build());
                log.info("Папка {} была создана в бакете {}", folderName, bucketName);
            }
        } catch (Exception e) {
            log.error("Ошибка при создании папки {} в бакете {}", folderName, bucketName, e);
            throw new RuntimeException("Ошибка при создании папки в Minio", e);
        }
    }
}
