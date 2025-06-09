package com.example.satrect.entity;

import java.time.LocalDateTime;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "images")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Image {
    @Id
    String image_id;
    String image_key;
    String name;
    String status;
    // check kkkkkk
    LocalDateTime created_at;
    LocalDateTime updated_at;
}
