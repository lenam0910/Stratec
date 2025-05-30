package com.example.satrect.service;

import org.springframework.web.multipart.MultipartFile;

import com.example.satrect.dto.request.ImageRequest;
import com.example.satrect.dto.response.ImageResponse;
import com.example.satrect.entity.Image;

public interface ImageService {
    ImageResponse postImage(MultipartFile imagePath, String imageName);

    ImageResponse getImageById(String imageId);
}
