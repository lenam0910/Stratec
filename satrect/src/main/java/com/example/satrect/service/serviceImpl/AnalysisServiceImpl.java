package com.example.satrect.service.serviceImpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.satrect.entity.Analysis;
import com.example.satrect.repository.AnalysisRepository;
import com.example.satrect.service.AnalysisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisRepository analysisRepository;

    @Override
    public List<Analysis> getAllAnalyses() {
        return analysisRepository.findAll();
    }

    @Override
    public Analysis getAnalysisById(String id) {
        return analysisRepository.findById(id).orElse(null);
    }

}
