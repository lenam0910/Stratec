package com.example.satrect.mapper;

import org.mapstruct.Mapper;

import com.example.satrect.dto.request.ImageRequest;
import com.example.satrect.dto.response.ImageResponse;
import com.example.satrect.entity.Image;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse toImageResponse(Image image);

    Image toImage(ImageRequest imageRequest);
}
