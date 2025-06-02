package com.example.satrect.controller;

import com.example.satrect.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AnalysisResultController {

    private final AnalysisResultService analysisResultService;

    @GetMapping("path")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

}
