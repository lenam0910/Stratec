package com.example.satrect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)

public class ImageResponse {
    String image_id;
    String image_key;
    String name;
    String status;
    String created_at;
    String updated_at;
    String imageData;
}
