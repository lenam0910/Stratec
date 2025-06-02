package com.example.satrect.service.serviceImpl;

import java.io.InputStream;
import java.time.LocalDateTime;

import io.minio.RestoreObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

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
            // Upload lên MinIO
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(uniqueImageId) // Lưu với uniqueImageId
                            .stream(imagePath.getInputStream(), imagePath.getSize(), -1)
                            .contentType(imagePath.getContentType())
                            .build());

            image.setImage_key(imageName);

            // Gửi ảnh lên Gemini để phân tích
            String geminiResult = analyzeImageWithGemini(imagePath);

            // Lưu kết quả phân tích (ví dụ set vào trường nào đó trong entity nếu cần)
            image.setStatus("Analyzed");
            // Bạn có thể thêm trường mới để lưu kết quả phân tích hoặc lưu ở chỗ khác
            log.info("Gemini phân tích kết quả: {}", geminiResult);

        } catch (Exception e) {
            log.error("Lỗi khi tải ảnh lên MinIO hoặc phân tích Gemini: {}", e.getMessage());
            throw new RuntimeException("Không thể tải ảnh lên MinIO hoặc phân tích Gemini", e);
        }

        imageRepository.save(image);
        log.info("Chi tiết ảnh: {}", image.toString());
        return imageMapper.toImageResponse(image);
    }

    private String analyzeImageWithGemini(MultipartFile imageFile) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", "Bearer " + apiKey);

            // Tạo body request với file Multipart
            org.springframework.util.LinkedMultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            // Gửi ảnh dưới dạng file multipart
            body.add("image", new org.springframework.core.io.ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return imageFile.getOriginalFilename();
                }
            });

            HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody(); // Trả về kết quả phân tích dưới dạng JSON string hoặc tuỳ API
            } else {
                throw new RuntimeException("Lỗi phản hồi từ Gemini: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi API Gemini: {}", e.getMessage());
            throw new RuntimeException("Không thể phân tích ảnh với Gemini", e);
        }
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