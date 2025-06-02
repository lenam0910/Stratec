package com.example.satrect.service.serviceImpl;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.example.satrect.entity.Analysis;
import com.example.satrect.entity.AnalysisResult;
import com.example.satrect.repository.AnalysisRepository;
import com.example.satrect.repository.AnalysisResultRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final AnalysisRepository analysisRepository;
    private final AnalysisResultRepository analysisResultRepository;
    private final ImageMapper imageMapper;
    private final MinioConfig minioConfig;
    private final MinioClient client;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Override
    public ImageResponse postImage(MultipartFile imagePath, String imageName) {
        String originalFilename = imagePath.getOriginalFilename();
        String uniqueImageId = originalFilename + "_" + System.currentTimeMillis();

        log.info("Image ID: {}", uniqueImageId);

        // Tạo entity Image
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
                            .object(uniqueImageId)
                            .stream(imagePath.getInputStream(), imagePath.getSize(), -1)
                            .contentType(imagePath.getContentType())
                            .build());

            image.setImage_key(imageName);

            // Lưu image vào database trước để có image_id
            imageRepository.save(image);

            // Gửi ảnh lên Gemini để phân tích
            String geminiResult = analyzeImageWithGemini(imagePath);

            // Parse và lưu kết quả phân tích
            Analysis analysis = new Analysis();
            analysis.setAnalysis_id(UUID.randomUUID().toString());
            analysis.setImage(image);
            analysisRepository.save(analysis);

            List<AnalysisResult> results = parseGeminiResult(geminiResult, analysis);
            analysisResultRepository.saveAll(results);

            image.setStatus("Analyzed");
            imageRepository.save(image);

            log.info("Gemini phân tích kết quả: {}", geminiResult);

        } catch (Exception e) {
            log.error("Lỗi khi tải ảnh lên MinIO hoặc phân tích Gemini: {}", e.getMessage());
            throw new RuntimeException("Không thể tải ảnh lên MinIO hoặc phân tích Gemini", e);
        }

        log.info("Chi tiết ảnh: {}", image.toString());
        return imageMapper.toImageResponse(image);
    }

    private String analyzeImageWithGemini(MultipartFile imageFile) {
        try {
            // Thiết lập header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON); // Gemini yêu cầu JSON

            // Chuyển ảnh thành base64
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());

            // Tạo JSON body cho Gemini API
            String jsonBody = String.format(
                    "{" +
                            "  \"contents\": [" +
                            "    {" +
                            "      \"parts\": [" +
                            "        {" +
                            "          \"text\": \"Describe this image and identify objects in it\"" +
                            "        }," +
                            "        {" +
                            "          \"inlineData\": {" +
                            "            \"mimeType\": \"%s\"," +
                            "            \"data\": \"%s\"" +
                            "          }" +
                            "        }" +
                            "      ]" +
                            "    }" +
                            "  ]" +
                            "}",
                    imageFile.getContentType(), base64Image
            );

            // Tạo request entity
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            // Thêm API key vào URL
            String urlWithKey = apiUrl + "?key=" + apiKey;
            log.info("Calling Gemini API at: {}", urlWithKey); // Log để debug

            // Gửi request
            ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Gemini API response: {}", response.getBody());
                return response.getBody();
            } else {
                throw new RuntimeException("Lỗi phản hồi từ Gemini: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi API Gemini: {}", e.getMessage());
            throw new RuntimeException("Không thể phân tích ảnh với Gemini", e);
        }
    }

    private List<AnalysisResult> parseGeminiResult(String geminiResult, Analysis analysis) throws Exception {
        List<AnalysisResult> results = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(geminiResult);

        // Giả sử Gemini trả về kết quả trong "candidates" -> "content" -> "parts" -> "text"
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode content = candidates.get(0).path("content");
            JsonNode parts = content.path("parts");
            if (parts.isArray()) {
                for (JsonNode part : parts) {
                    String text = part.path("text").asText(null);
                    if (text != null) {
                        // Giả sử text chứa mô tả, cần parse thêm để lấy các đối tượng
                        AnalysisResult result = new AnalysisResult();
                        result.setObject_id(UUID.randomUUID().toString());
                        result.setTitle(text); // Có thể cần parse text để lấy title cụ thể
                        result.setSupercategory("Unknown"); // Điều chỉnh dựa trên phản hồi
                        result.setCategory("Unknown");
                        result.setSubcategory("Unknown");
                        result.setType("Unknown");
                        result.setScore(0.0); // Gemini có thể trả về confidence score
                        result.setLength(0.0); // Điều chỉnh nếu có thông tin
                        result.setClass_id("Unknown");
                        result.setFighter_classification("N/A");
                        result.setAnalysis(analysis);
                        results.add(result);
                    }
                }
            }
        }
        return results;
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

        log.info("Số ảnh từ database: {}", images.size());
        for (var item : images) {
            log.info("Xử lý ảnh, ID: {}", item.getImage_id());
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
        log.info("Số ảnh trả về: {}", lstImgResponse.size());
        return lstImgResponse;
    }
}