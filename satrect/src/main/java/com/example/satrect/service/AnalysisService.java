package com.example.satrect.service;

import java.util.List;

import com.example.satrect.entity.Analysis;

public interface AnalysisService {
    List<Analysis> getAllAnalyses();

    Analysis getAnalysisById(String id);
}
