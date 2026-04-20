package com.stefan.riskplatform.alert.dto;

import com.stefan.riskplatform.common.enums.AlertStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAlertStatusRequest {

    @NotNull
    private AlertStatus status;
}