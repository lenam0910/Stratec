package com.example.satrect.service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.satrect.configuration.MinioConfig;
import com.example.satrect.dto.request.ImageRequest;
import com.example.satrect.dto.response.ImageResponse;
import com.example.satrect.entity.Image;
import com.example.satrect.mapper.ImageMapper;
import com.example.satrect.repository.ImageRepository;
import com.example.satrect.service.ImageService;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final MinioConfig minioConfig;
    private final MinioClient client;

    @Override
    public ImageResponse postImage(MultipartFile imagePath, String imageName) {
        String originalFilename = imagePath.getOriginalFilename();

        String uniqueImageId = originalFilename + "_" + System.currentTimeMillis();

        log.info("Image ID: {}", uniqueImageId);

        Image image = Image.builder()
                .image_id(uniqueImageId)
                .name(originalFilename)
                .status("Processing")
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();
        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(originalFilename)
                            .stream(imagePath.getInputStream(), imagePath.getSize(), -1)
                            .contentType(imagePath.getContentType())
                            .build());

            image.setImage_key(imageName);
        } catch (Exception e) {
            log.info("Error uploading to MinIO: {}", e.getMessage());
            throw new RuntimeException("Failed to upload image to MinIO", e);
        }

        imageRepository.save(image);
        log.info("Image Detail: {}", image.toString());
        return imageMapper.toImageResponse(image);
    }

    @Override
    public ImageResponse getImageById(String imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        String objectName = minioConfig.getBucketName();
        String url;
        try {
            url = client.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .method(io.minio.http.Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .expiry(60 * 60)
                            .build());
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate image URL", e);
        }

        ImageResponse response = imageMapper.toImageResponse(image);
        return response;
    }

}
