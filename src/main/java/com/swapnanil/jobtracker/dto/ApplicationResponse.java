package com.swapnanil.jobtracker.dto;

import com.swapnanil.jobtracker.entity.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        String company,
        String position,
        ApplicationStatus status,
        LocalDate appliedDate,
        String notes,
        LocalDateTime createdAt,
        String ownerEmail) {
}
