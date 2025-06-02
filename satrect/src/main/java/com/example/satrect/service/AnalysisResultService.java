package com.example.satrect.service;

import com.example.satrect.entity.AnalysisResult;

import java.util.List;

public interface AnalysisResultService {
    List<AnalysisResult> getAResultByAnalysisId(String analysisId);

}
