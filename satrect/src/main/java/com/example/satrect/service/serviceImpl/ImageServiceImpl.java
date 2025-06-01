package com.example.satrect.service.serviceImpl;

import java.io.InputStream;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.satrect.configuration.MinioConfig;
import com.example.satrect.dto.response.ImageResponse;
import com.example.satrect.entity.Image;
import com.example.satrect.mapper.ImageMapper;
import com.example.satrect.repository.ImageRepository;
import com.example.satrect.service.ImageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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
                            .object(uniqueImageId) // Lưu với uniqueImageId
                            .stream(imagePath.getInputStream(), imagePath.getSize(), -1)
                            .contentType(imagePath.getContentType())
                            .build());

            image.setImage_key(imageName);
        } catch (Exception e) {
            log.error("Lỗi khi tải ảnh lên MinIO: {}", e.getMessage());
            throw new RuntimeException("Không thể tải ảnh lên MinIO", e);
        }

        imageRepository.save(image);
        log.info("Chi tiết ảnh: {}", image.toString());
        return imageMapper.toImageResponse(image);
    }

    @Override
    public ImageResponse getImageById(String imageId) {
        // Tìm ảnh trong database
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với id: " + imageId));

        String imageDataBase64;
        try {
            // Tải ảnh từ MinIO
            try (InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(imageId)
                            .build())) {
                byte[] imageBytes = inputStream.readAllBytes();
                imageDataBase64 = Base64.getEncoder().encodeToString(imageBytes);
            }
        } catch (Exception e) {
            log.error("Lỗi khi tải ảnh từ MinIO: {}", e.getMessage());
            throw new RuntimeException("Không thể tải ảnh từ MinIO", e);
        }

        ImageResponse response = imageMapper.toImageResponse(image);
        response.setImageData(imageDataBase64);
        return response;
    }



    @Override
    public List<ImageResponse> getAllImages() {
        // Tìm tất cả ảnh trong database
        List<Image> images = imageRepository.findAll();
        List<ImageResponse> lstImgResponse = new ArrayList<>();
        String imageDataBase64;

        log.info("Số ảnh từ database: {}", images.size()); // Debug
        for (var item : images) {
            log.info("Xử lý ảnh, ID: {}", item.getImage_id()); // Debug
            try {
                try (InputStream inputStream = client.getObject(
                        GetObjectArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .object(item.getImage_id())
                                .build())) {
                    byte[] imageBytes = inputStream.readAllBytes();
                    imageDataBase64 = Base64.getEncoder().encodeToString(imageBytes);
                }
                ImageResponse response = imageMapper.toImageResponse(item);
                response.setImageData(imageDataBase64);
                lstImgResponse.add(response);
            } catch (Exception e) {
                log.error("Lỗi khi tải ảnh {} từ MinIO: {}", item.getImage_id(), e.getMessage());
            }
        }
        log.info("Số ảnh trả về: {}", lstImgResponse.size()); // Debug
        return lstImgResponse;
    }
}