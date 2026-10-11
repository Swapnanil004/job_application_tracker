package com.swapnanil.jobtracker.controller;

import com.swapnanil.jobtracker.dto.ApplicationRequest;
import com.swapnanil.jobtracker.dto.ApplicationResponse;
import com.swapnanil.jobtracker.dto.PageResponse;
import com.swapnanil.jobtracker.dto.StatusUpdateRequest;
import com.swapnanil.jobtracker.entity.ApplicationStatus;
import com.swapnanil.jobtracker.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Job Applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(summary = "Create a job application (starts as APPLIED)")
    @PostMapping
    public ResponseEntity<ApplicationResponse> create(Authentication authentication,
                                                      @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.create(authentication.getName(), request));
    }

    @Operation(summary = "List your applications with optional status/company filter, pagination and sorting")
    @GetMapping
    public PageResponse<ApplicationResponse> list(
            Authentication authentication,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String company,
            @PageableDefault(size = 10, sort = "appliedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return applicationService.list(authentication.getName(), status, company, pageable);
    }

    @Operation(summary = "Get one application")
    @GetMapping("/{id}")
    public ApplicationResponse get(Authentication authentication, @PathVariable Long id) {
        return applicationService.get(authentication.getName(), id);
    }

    @Operation(summary = "Update company, position, date and notes")
    @PutMapping("/{id}")
    public ApplicationResponse update(Authentication authentication, @PathVariable Long id,
                                      @Valid @RequestBody ApplicationRequest request) {
        return applicationService.update(authentication.getName(), id, request);
    }

    @Operation(summary = "Move an application through the status workflow")
    @PatchMapping("/{id}/status")
    public ApplicationResponse updateStatus(Authentication authentication, @PathVariable Long id,
                                            @Valid @RequestBody StatusUpdateRequest request) {
        return applicationService.updateStatus(authentication.getName(), id, request);
    }

    @Operation(summary = "Delete an application")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        applicationService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
