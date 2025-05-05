package dev.clinic.mainservice.services;

import io.minio.*;
import net.coobird.thumbnailator.Thumbnails;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class ImageUploaderService {

    private final MinioClient minioClient;
    private final String bucketName = "images";

    @Value("localhost")
    private String endpoint;

    public ImageUploaderService(MinioClient minioClient) {
        this.minioClient = minioClient;

        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка подключения к MinIO: " + e.getMessage(), e);
        }
    }

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            if (originalImage == null) {
                throw new IllegalArgumentException("Invalid image file");
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(originalImage.getWidth(), originalImage.getHeight())
                    .outputFormat("jpg")
                    .outputQuality(0.7)
                    .toOutputStream(os);

            byte[] imageData = os.toByteArray();

            // Генерация уникального имени файла
            String objectName = "client_" + UUID.randomUUID() + ".jpg";

            // Загрузка в Minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(imageData), imageData.length, -1)
                            .contentType("image/jpeg")
                            .build());

            return endpoint + "/" + bucketName + "/" + objectName;
        } catch (Exception e) {
            throw new ServiceException("Image upload failed", e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String objectName = extractObjectNameFromUrl(imageUrl);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new ServiceException("Image deletion failed", e);
        }
    }

    private String extractObjectNameFromUrl(String imageUrl) {
        return imageUrl.replace(endpoint + "/" + bucketName + "/", "");
    }
}

