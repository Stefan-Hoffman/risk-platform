package com.stefan.riskplatform.assessment.repository;

import com.stefan.riskplatform.assessment.entity.RuleHit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleHitRepository extends JpaRepository<RuleHit, Long> {

    List<RuleHit> findByAssessment_AssessmentId(String assessmentId);
}