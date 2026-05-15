package com.stefan.riskplatform.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.alert.service.AlertService;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.assessment.repository.RiskAssessmentRepository;
import com.stefan.riskplatform.assessment.repository.RuleHitRepository;
import com.stefan.riskplatform.common.exception.InvalidPayloadException;
import com.stefan.riskplatform.common.exception.ResourceNotFoundException;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.entityrecord.service.EntityRecordService;
import com.stefan.riskplatform.event.dto.EventAcceptedResponse;
import com.stefan.riskplatform.event.dto.EventRequest;
import com.stefan.riskplatform.event.dto.EventResponse;
import com.stefan.riskplatform.event.entity.Event;
import com.stefan.riskplatform.event.mapper.EventMapper;
import com.stefan.riskplatform.event.repository.EventRepository;
import com.stefan.riskplatform.risk.service.RiskEngineService;
import com.stefan.riskplatform.rule.service.RiskRuleService;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final TenantService tenantService;
    private final EntityRecordService entityRecordService;
    private final ObjectMapper objectMapper;
    private final RiskRuleService riskRuleService;
    private final RiskEngineService riskEngineService;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final AlertService alertService;
    private final RuleHitRepository ruleHitRepository;

    public EventAcceptedResponse ingestEvent(String tenantId, EventRequest request) {
        log.info("Ingesting event. tenantId={}, entityId={}, eventType={}",
                tenantId, request.getEntityId(), request.getEventType());

        Tenant tenant = tenantService.getTenantOrThrow(tenantId);
        EntityRecord entityRecord = entityRecordService.getEntityRecordByTenantOrThrow(
                request.getEntityId(),
                tenantId
        );

        Instant now = Instant.now();
        String payloadJson = convertPayloadToJson(request);

        Event event = Event.builder()
                .eventId(UUID.randomUUID().toString())
                .tenant(tenant)
                .entityRecord(entityRecord)
                .eventType(request.getEventType())
                .eventTimestamp(now)
                .source(request.getSource())
                .ipAddress(request.getIpAddress())
                .deviceId(request.getDeviceId())
                .payloadJson(payloadJson)
                .createdAt(now)
                .build();

        Event savedEvent = eventRepository.save(event);

        log.info("Event saved. eventId={}, tenantId={}", savedEvent.getEventId(), tenantId);

        var rules = riskRuleService.getEnabledRuleEntities(tenantId, request.getEventType());

        log.debug("Loaded rules for event. tenantId={}, eventType={}, ruleCount={}",
                tenantId, request.getEventType(), rules.size());

        var matchedRules = riskEngineService.evaluateMatchedRules(rules, payloadJson);
        int score = matchedRules.stream().mapToInt(r -> r.getRiskScore()).sum();

        var decision = score >= 80
                ? com.stefan.riskplatform.common.enums.RiskDecision.BLOCK
                : score >= 50
                  ? com.stefan.riskplatform.common.enums.RiskDecision.REVIEW
                  : com.stefan.riskplatform.common.enums.RiskDecision.ALLOW;

        RiskAssessment assessment = RiskAssessment.builder()
                .assessmentId(UUID.randomUUID().toString())
                .event(savedEvent)
                .tenant(tenant)
                .totalScore(score)
                .decision(decision)
                .evaluatedAt(now)
                .build();

        RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);

        log.info("Risk assessment saved. assessmentId={}, eventId={}, score={}, decision={}, matchedRules={}",
                savedAssessment.getAssessmentId(),
                savedEvent.getEventId(),
                score,
                decision,
                matchedRules.size());

        for (var matchedRule : matchedRules) {
            ruleHitRepository.save(
                    com.stefan.riskplatform.assessment.entity.RuleHit.builder()
                            .assessment(savedAssessment)
                            .rule(matchedRule)
                            .riskScore(matchedRule.getRiskScore())
                            .build()
            );
        }

        if (score >= 50) {
            alertService.createAlert(tenant, entityRecord, savedAssessment, score);

            log.warn("Alert created for risky event. eventId={}, assessmentId={}, score={}, decision={}",
                    savedEvent.getEventId(),
                    savedAssessment.getAssessmentId(),
                    score,
                    decision);
        }

        return EventAcceptedResponse.builder()
                .eventId(savedEvent.getEventId())
                .assessmentId(savedAssessment.getAssessmentId())
                .status("ACCEPTED")
                .build();
    }

    public EventResponse getEventById(String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + eventId));

        return eventMapper.toResponse(event);
    }

    private String convertPayloadToJson(EventRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getPayload());
        } catch (JsonProcessingException e) {
            throw new InvalidPayloadException("Failed to serialize event payload", e);
        }
    }
}