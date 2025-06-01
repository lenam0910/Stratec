package com.example.satrect.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "detected_objects")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AnalysisResult {
    @Id
    String object_id;
    String title;
    String supercategory;
    String category;
    String subcategory;
    String type;
    Double score;
    Double length;
    String class_id;
    String fighter_classification;

    @ManyToOne
    @JoinColumn(name = "analysis_id", referencedColumnName = "analysis_id")
    Analysis analysis; // Liên kết với bản ghi phân tích
}
