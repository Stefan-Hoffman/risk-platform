package com.stefan.riskplatform.assessment.repository;

import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, String> {

    List<RiskAssessment> findByTenant_TenantId(String tenantId);
}