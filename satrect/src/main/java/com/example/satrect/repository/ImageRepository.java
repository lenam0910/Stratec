package com.example.satrect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.satrect.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {

}
