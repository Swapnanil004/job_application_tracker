package com.swapnanil.jobtracker.service;

import com.swapnanil.jobtracker.dto.ApplicationRequest;
import com.swapnanil.jobtracker.dto.ApplicationResponse;
import com.swapnanil.jobtracker.dto.StatusUpdateRequest;
import com.swapnanil.jobtracker.entity.ApplicationStatus;
import com.swapnanil.jobtracker.entity.JobApplication;
import com.swapnanil.jobtracker.entity.Role;
import com.swapnanil.jobtracker.entity.User;
import com.swapnanil.jobtracker.exception.InvalidStatusTransitionException;
import com.swapnanil.jobtracker.exception.ResourceNotFoundException;
import com.swapnanil.jobtracker.repository.JobApplicationRepository;
import com.swapnanil.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private JobApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test User", "test@example.com", "hashed", Role.USER);
        user.setId(1L);
    }

    @Test
    void createStartsWithAppliedStatus() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(applicationRepository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        ApplicationRequest request =
                new ApplicationRequest("Cognizant", "Software Engineer", LocalDate.of(2026, 10, 12), "GenC");

        ApplicationResponse response = applicationService.create("test@example.com", request);

        assertEquals(ApplicationStatus.APPLIED, response.status());
        assertEquals("Cognizant", response.company());
        assertEquals("test@example.com", response.ownerEmail());
    }

    @Test
    void getThrowsWhenApplicationNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(applicationRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> applicationService.get("test@example.com", 99L));
    }

    @Test
    void updateStatusAllowsValidTransition() {
        JobApplication application = sampleApplication(ApplicationStatus.APPLIED);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(applicationRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(JobApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        ApplicationResponse response = applicationService.updateStatus(
                "test@example.com", 5L, new StatusUpdateRequest(ApplicationStatus.INTERVIEW));

        assertEquals(ApplicationStatus.INTERVIEW, response.status());
        verify(applicationRepository).save(application);
    }

    @Test
    void updateStatusRejectsInvalidTransition() {
        JobApplication application = sampleApplication(ApplicationStatus.REJECTED);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(applicationRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(application));

        assertThrows(InvalidStatusTransitionException.class, () -> applicationService.updateStatus(
                "test@example.com", 5L, new StatusUpdateRequest(ApplicationStatus.OFFER)));
    }

    private JobApplication sampleApplication(ApplicationStatus status) {
        JobApplication application = new JobApplication();
        application.setId(5L);
        application.setCompany("Infosys");
        application.setPosition("Developer");
        application.setAppliedDate(LocalDate.of(2026, 10, 1));
        application.setStatus(status);
        application.setUser(user);
        return application;
    }
}
