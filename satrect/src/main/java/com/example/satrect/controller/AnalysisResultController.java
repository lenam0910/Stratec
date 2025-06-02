package com.example.satrect.controller;

import com.example.satrect.service.AnalysisResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AnalysisResultController {

    private final AnalysisResultService analysisResultService;
}
