package com.example.satrect.controller;

import com.example.satrect.dto.response.ApiResponse;
import com.example.satrect.service.AnalysisResultService;
import com.example.satrect.utils.Notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class AnalysisResultController {

    private final AnalysisResultService analysisResultService;

    @GetMapping("result/{id}")
    public ApiResponse<Object> getAnalysisResult(@PathVariable("id") String id) {
        return ApiResponse.builder()
                .code(1000)
                .message(Notification.GET_RESULT_SUCCESS)
                .data(analysisResultService.getAResultByAnalysisId(id))
                .build();
    }

}
