package com.example.satrect.controller;

import org.springframework.stereotype.Controller;

import com.example.satrect.dto.response.ApiResponse;
import com.example.satrect.repository.AnalysisRepository;
import com.example.satrect.service.AnalysisService;
import com.example.satrect.utils.Notification;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    @GetMapping("analyses/{anaId}")
    public ApiResponse<Object> getAllAnalyses(@PathVariable("anaId") String analysisID) {
        return ApiResponse.builder()
                .code(1000)
                .message(Notification.GET_ANALYSIS_SUCCESS)
                .data(analysisService.getAnalysisById(analysisID))
                .build();
    }

}
