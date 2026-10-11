package com.swapnanil.jobtracker.dto;

import com.swapnanil.jobtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull(message = "Status is required") ApplicationStatus status) {
}
