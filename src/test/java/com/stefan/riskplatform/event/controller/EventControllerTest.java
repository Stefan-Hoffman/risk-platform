package com.stefan.riskplatform.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.riskplatform.event.dto.EventAcceptedResponse;
import com.stefan.riskplatform.event.dto.EventRequest;
import com.stefan.riskplatform.event.dto.EventResponse;
import com.stefan.riskplatform.event.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void shouldIngestEvent() throws Exception {
        EventRequest request = new EventRequest();
        request.setEventType("LOGIN");
        request.setEntityId("user_123");
        request.setSource("web-app");
        request.setIpAddress("192.168.1.4");
        request.setDeviceId("device_1");
        request.setPayload(Map.of("knownDevice", false));

        EventAcceptedResponse response = EventAcceptedResponse.builder()
                .eventId("event_1")
                .status("ACCEPTED")
                .build();

        when(eventService.ingestEvent(eq("tenant_1"), any(EventRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/events")
                        .header("X-Tenant-Id", "tenant_1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.eventId").value("event_1"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void shouldGetEventById() throws Exception {
        EventResponse response = EventResponse.builder()
                .eventId("event_1")
                .tenantId("tenant_1")
                .entityId("user_123")
                .eventType("LOGIN")
                .eventTimestamp(Instant.now())
                .source("web-app")
                .payloadJson("{\"knownDevice\":false}")
                .createdAt(Instant.now())
                .build();

        when(eventService.getEventById("event_1")).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/event_1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value("event_1"))
                .andExpect(jsonPath("$.entityId").value("user_123"));
    }
}