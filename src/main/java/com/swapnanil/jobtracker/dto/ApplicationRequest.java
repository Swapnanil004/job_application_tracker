package com.swapnanil.jobtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ApplicationRequest(
        @NotBlank(message = "Company is required") @Size(max = 150) String company,
        @NotBlank(message = "Position is required") @Size(max = 150) String position,
        @NotNull(message = "Applied date is required (yyyy-MM-dd)") LocalDate appliedDate,
        @Size(max = 1000, message = "Notes can be at most 1000 characters") String notes) {
}
