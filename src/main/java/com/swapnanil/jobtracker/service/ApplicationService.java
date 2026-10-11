package com.swapnanil.jobtracker.service;

import com.swapnanil.jobtracker.dto.ApplicationRequest;
import com.swapnanil.jobtracker.dto.ApplicationResponse;
import com.swapnanil.jobtracker.dto.PageResponse;
import com.swapnanil.jobtracker.dto.StatusUpdateRequest;
import com.swapnanil.jobtracker.entity.ApplicationStatus;
import com.swapnanil.jobtracker.entity.JobApplication;
import com.swapnanil.jobtracker.entity.User;
import com.swapnanil.jobtracker.exception.InvalidStatusTransitionException;
import com.swapnanil.jobtracker.exception.ResourceNotFoundException;
import com.swapnanil.jobtracker.repository.JobApplicationRepository;
import com.swapnanil.jobtracker.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(JobApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApplicationResponse create(String email, ApplicationRequest request) {
        JobApplication application = new JobApplication();
        application.setUser(findUser(email));
        application.setStatus(ApplicationStatus.APPLIED);
        copyFields(application, request);
        return toResponse(applicationRepository.save(application));
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> list(String email, ApplicationStatus status, String company,
                                                  Pageable pageable) {
        Long userId = findUser(email).getId();
        Specification<JobApplication> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (company != null && !company.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("company")), "%" + company.trim().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<JobApplication> page = applicationRepository.findAll(spec, pageable);
        return PageResponse.from(page.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(String email, Long id) {
        return toResponse(findOwned(email, id));
    }

    @Transactional
    public ApplicationResponse update(String email, Long id, ApplicationRequest request) {
        JobApplication application = findOwned(email, id);
        copyFields(application, request);
        return toResponse(applicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse updateStatus(String email, Long id, StatusUpdateRequest request) {
        JobApplication application = findOwned(email, id);
        ApplicationStatus current = application.getStatus();
        ApplicationStatus next = request.status();
        if (!current.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status from " + current + " to " + next);
        }
        application.setStatus(next);
        return toResponse(applicationRepository.save(application));
    }

    @Transactional
    public void delete(String email, Long id) {
        applicationRepository.delete(findOwned(email, id));
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> listAll(Pageable pageable) {
        return PageResponse.from(applicationRepository.findAll(pageable).map(this::toResponse));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private JobApplication findOwned(String email, Long id) {
        Long userId = findUser(email).getId();
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    private void copyFields(JobApplication application, ApplicationRequest request) {
        application.setCompany(request.company().trim());
        application.setPosition(request.position().trim());
        application.setAppliedDate(request.appliedDate());
        application.setNotes(request.notes());
    }

    private ApplicationResponse toResponse(JobApplication a) {
        return new ApplicationResponse(a.getId(), a.getCompany(), a.getPosition(), a.getStatus(),
                a.getAppliedDate(), a.getNotes(), a.getCreatedAt(), a.getUser().getEmail());
    }
}
