package com.example.satrect.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import com.example.satrect.dto.response.ApiResponse;
import com.example.satrect.dto.response.ImageResponse;
import com.example.satrect.service.ImageService;
import com.example.satrect.utils.Notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("img")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload-image")
    public ApiResponse<Object> uploadImage(@RequestParam("image") MultipartFile image,
            @RequestParam("imageName") String imageName) {
        ImageResponse imageResponse = imageService.postImage(image, imageName);
        return ApiResponse.builder()
                .code(1000)
                .message(Notification.UPLOAD_IMAGE_SUCCESS)
                .data(imageResponse)
                .build();
    }

    @GetMapping("{id}")
    public ApiResponse<Object> getImage(@PathVariable("id") String image_id) {
        ImageResponse imageResponse = imageService.getImageById(image_id);
        return ApiResponse.builder()
                .code(1000)
                .data(imageResponse)
                .message(Notification.GET_IMAGE_SUCCESS)
                .build();
    }

    @GetMapping
    public ApiResponse<Object> getAllImages() {
        return ApiResponse.builder()
                .code(1000)
                .message("Get all images")
                .data(imageService.getAllImages())
                .build();
    }

}
