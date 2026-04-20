package com.stefan.riskplatform.event.repository;

import com.stefan.riskplatform.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    List<Event> findByEntityRecord_EntityId(String entityId);

    List<Event> findByTenant_TenantIdAndEventType(String tenantId, String eventType);
}