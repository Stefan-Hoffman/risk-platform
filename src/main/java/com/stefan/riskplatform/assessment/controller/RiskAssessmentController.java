package com.stefan.riskplatform.assessment.controller;

import com.stefan.riskplatform.assessment.dto.RiskAssessmentResponse;
import com.stefan.riskplatform.assessment.service.RiskAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    @GetMapping("/{assessmentId}")
    public ResponseEntity<RiskAssessmentResponse> getAssessmentById(@PathVariable String assessmentId) {
        return ResponseEntity.ok(riskAssessmentService.getAssessmentById(assessmentId));
    }
}