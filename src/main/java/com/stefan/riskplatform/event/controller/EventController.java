package com.stefan.riskplatform.event.controller;

import com.stefan.riskplatform.event.dto.EventAcceptedResponse;
import com.stefan.riskplatform.event.dto.EventRequest;
import com.stefan.riskplatform.event.dto.EventResponse;
import com.stefan.riskplatform.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventAcceptedResponse> ingestEvent(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @Valid @RequestBody EventRequest request
    ) {
        EventAcceptedResponse response = eventService.ingestEvent(tenantId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable String eventId) {
        EventResponse response = eventService.getEventById(eventId);
        return ResponseEntity.ok(response);
    }
}