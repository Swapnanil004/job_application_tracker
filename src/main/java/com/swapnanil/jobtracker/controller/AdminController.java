package com.swapnanil.jobtracker.controller;

import com.swapnanil.jobtracker.dto.ApplicationResponse;
import com.swapnanil.jobtracker.dto.PageResponse;
import com.swapnanil.jobtracker.dto.UserResponse;
import com.swapnanil.jobtracker.service.ApplicationService;
import com.swapnanil.jobtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin-only endpoints (ROLE_ADMIN is enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final ApplicationService applicationService;
    private final UserService userService;

    public AdminController(ApplicationService applicationService, UserService userService) {
        this.applicationService = applicationService;
        this.userService = userService;
    }

    @Operation(summary = "List all users (admin only)")
    @GetMapping("/users")
    public List<UserResponse> users() {
        return userService.listUsers();
    }

    @Operation(summary = "List applications of all users (admin only)")
    @GetMapping("/applications")
    public PageResponse<ApplicationResponse> allApplications(
            @PageableDefault(size = 10, sort = "appliedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return applicationService.listAll(pageable);
    }
}
