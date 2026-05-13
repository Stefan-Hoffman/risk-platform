package com.stefan.riskplatform.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.alert.service.AlertService;
import com.stefan.riskplatform.assessment.entity.RiskAssessment;
import com.stefan.riskplatform.assessment.entity.RuleHit;
import com.stefan.riskplatform.assessment.repository.RiskAssessmentRepository;
import com.stefan.riskplatform.assessment.repository.RuleHitRepository;
import com.stefan.riskplatform.common.enums.RiskDecision;
import com.stefan.riskplatform.common.enums.TenantStatus;
import com.stefan.riskplatform.entityrecord.entity.EntityRecord;
import com.stefan.riskplatform.entityrecord.service.EntityRecordService;
import com.stefan.riskplatform.event.dto.EventAcceptedResponse;
import com.stefan.riskplatform.event.dto.EventRequest;
import com.stefan.riskplatform.event.dto.EventResponse;
import com.stefan.riskplatform.event.entity.Event;
import com.stefan.riskplatform.event.mapper.EventMapper;
import com.stefan.riskplatform.event.repository.EventRepository;
import com.stefan.riskplatform.risk.service.RiskEngineService;
import com.stefan.riskplatform.rule.entity.RiskRule;
import com.stefan.riskplatform.rule.service.RiskRuleService;
import com.stefan.riskplatform.tenant.entity.Tenant;
import com.stefan.riskplatform.tenant.service.TenantService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private TenantService tenantService;

    @Mock
    private EntityRecordService entityRecordService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RiskRuleService riskRuleService;

    @Mock
    private RiskEngineService riskEngineService;

    @Mock
    private RiskAssessmentRepository riskAssessmentRepository;

    @Mock
    private RuleHitRepository ruleHitRepository;

    @Mock
    private AlertService alertService;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldIngestEventAndCreateAssessmentAndReturnAssessmentId() throws Exception {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .tenant(tenant)
                .build();

        EventRequest request = new EventRequest();
        request.setEventType("LOGIN");
        request.setEntityId("user_123");
        request.setSource("web-app");
        request.setIpAddress("192.168.1.4");
        request.setDeviceId("device_1");
        request.setPayload(Map.of(
                "knownDevice", false,
                "country", "DE"
        ));

        Event savedEvent = Event.builder()
                .eventId("event_1")
                .tenant(tenant)
                .entityRecord(entityRecord)
                .eventType("LOGIN")
                .eventTimestamp(Instant.now())
                .source("web-app")
                .ipAddress("192.168.1.4")
                .deviceId("device_1")
                .payloadJson("{\"knownDevice\":false,\"country\":\"DE\"}")
                .createdAt(Instant.now())
                .build();

        RiskRule matchedRule = RiskRule.builder()
                .ruleId("rule_1")
                .eventType("LOGIN")
                .riskScore(60)
                .build();

        RiskAssessment savedAssessment = RiskAssessment.builder()
                .assessmentId("assessment_1")
                .event(savedEvent)
                .tenant(tenant)
                .totalScore(60)
                .decision(RiskDecision.REVIEW)
                .evaluatedAt(Instant.now())
                .build();

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(entityRecordService.getEntityRecordByTenantOrThrow("user_123", "tenant_1"))
                .thenReturn(entityRecord);
        when(objectMapper.writeValueAsString(request.getPayload()))
                .thenReturn("{\"knownDevice\":false,\"country\":\"DE\"}");
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(riskRuleService.getEnabledRuleEntities("tenant_1", "LOGIN"))
                .thenReturn(List.of(matchedRule));
        when(riskEngineService.evaluateMatchedRules(List.of(matchedRule), "{\"knownDevice\":false,\"country\":\"DE\"}"))
                .thenReturn(List.of(matchedRule));
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(savedAssessment);
        when(ruleHitRepository.save(any(RuleHit.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventAcceptedResponse result = eventService.ingestEvent("tenant_1", request);

        assertThat(result.getEventId()).isEqualTo("event_1");
        assertThat(result.getAssessmentId()).isEqualTo("assessment_1");
        assertThat(result.getStatus()).isEqualTo("ACCEPTED");

        verify(riskAssessmentRepository).save(any(RiskAssessment.class));
        verify(ruleHitRepository).save(any(RuleHit.class));
        verify(alertService).createAlert(eq(tenant), eq(entityRecord), eq(savedAssessment), eq(60));
    }

    @Test
    void shouldIngestEventWithoutCreatingAlertWhenScoreBelowThreshold() throws Exception {
        Tenant tenant = Tenant.builder()
                .tenantId("tenant_1")
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        EntityRecord entityRecord = EntityRecord.builder()
                .entityId("user_123")
                .tenant(tenant)
                .build();

        EventRequest request = new EventRequest();
        request.setEventType("LOGIN");
        request.setEntityId("user_123");
        request.setSource("web-app");
        request.setPayload(Map.of("knownDevice", true));

        Event savedEvent = Event.builder()
                .eventId("event_2")
                .tenant(tenant)
                .entityRecord(entityRecord)
                .eventType("LOGIN")
                .eventTimestamp(Instant.now())
                .source("web-app")
                .payloadJson("{\"knownDevice\":true}")
                .createdAt(Instant.now())
                .build();

        RiskAssessment savedAssessment = RiskAssessment.builder()
                .assessmentId("assessment_2")
                .event(savedEvent)
                .tenant(tenant)
                .totalScore(20)
                .decision(RiskDecision.ALLOW)
                .evaluatedAt(Instant.now())
                .build();

        when(tenantService.getTenantOrThrow("tenant_1")).thenReturn(tenant);
        when(entityRecordService.getEntityRecordByTenantOrThrow("user_123", "tenant_1"))
                .thenReturn(entityRecord);
        when(objectMapper.writeValueAsString(request.getPayload()))
                .thenReturn("{\"knownDevice\":true}");
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(riskRuleService.getEnabledRuleEntities("tenant_1", "LOGIN"))
                .thenReturn(List.of());
        when(riskEngineService.evaluateMatchedRules(List.of(), "{\"knownDevice\":true}"))
                .thenReturn(List.of());
        when(riskAssessmentRepository.save(any(RiskAssessment.class))).thenReturn(savedAssessment);

        EventAcceptedResponse result = eventService.ingestEvent("tenant_1", request);

        assertThat(result.getEventId()).isEqualTo("event_2");
        assertThat(result.getAssessmentId()).isEqualTo("assessment_2");
        assertThat(result.getStatus()).isEqualTo("ACCEPTED");

        verify(riskAssessmentRepository).save(any(RiskAssessment.class));
        verify(ruleHitRepository, never()).save(any(RuleHit.class));
        verify(alertService, never()).createAlert(any(), any(), any(), anyInt());
    }

    @Test
    void shouldReturnEventById() {
        Event event = Event.builder()
                .eventId("event_1")
                .build();

        EventResponse response = EventResponse.builder()
                .eventId("event_1")
                .build();

        when(eventRepository.findById("event_1")).thenReturn(Optional.of(event));
        when(eventMapper.toResponse(event)).thenReturn(response);

        EventResponse result = eventService.getEventById("event_1");

        assertThat(result.getEventId()).isEqualTo("event_1");
    }

    @Test
    void shouldThrowWhenEventNotFound() {
        when(eventRepository.findById("event_404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById("event_404"))
                .hasMessage("Event not found: event_404");
    }
}