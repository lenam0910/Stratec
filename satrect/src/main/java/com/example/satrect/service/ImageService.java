package com.example.satrect.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.satrect.dto.request.ImageRequest;
import com.example.satrect.dto.response.ImageResponse;
import com.example.satrect.entity.Image;

public interface ImageService {
    ImageResponse postImage(MultipartFile imagePath, String imageName);

    ImageResponse getImageById(String imageId);

    List<ImageResponse> getAllImages();
}
