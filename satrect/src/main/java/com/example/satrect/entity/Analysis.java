package com.example.satrect.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "analysis")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Analysis {
    @Id
    String analysis_id;
    String type;
    String status;
    String success_callback_url;
    String failure_callback_url;


    @OneToOne
    @JoinColumn(name = "image_id", referencedColumnName = "image_id")
    Image image;
    String name;
}
