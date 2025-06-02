package com.example.satrect.service.serviceImpl;

import com.example.satrect.entity.AnalysisResult;
import com.example.satrect.repository.AnalysisResultRepository;
import com.example.satrect.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisResultServiceImp implements AnalysisResultService {
    private final AnalysisResultRepository repository;

    @Override
    public List<AnalysisResult> getAResultByAnalysisId(String analysisId) {
        return  repository.findAll()
                .stream()
                .filter(x -> x.getAnalysis().getAnalysis_id().equalsIgnoreCase(analysisId))
                .toList();
    }
}
