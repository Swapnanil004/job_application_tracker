package com.swapnanil.jobtracker.service;

import com.swapnanil.jobtracker.entity.ApplicationStatus;
import org.junit.jupiter.api.Test;

import static com.swapnanil.jobtracker.entity.ApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationStatusTest {

    @Test
    void appliedCanMoveToInterviewOrRejected() {
        assertTrue(APPLIED.canTransitionTo(INTERVIEW));
        assertTrue(APPLIED.canTransitionTo(REJECTED));
        assertFalse(APPLIED.canTransitionTo(OFFER));
    }

    @Test
    void interviewCanMoveToOfferOrRejected() {
        assertTrue(INTERVIEW.canTransitionTo(OFFER));
        assertTrue(INTERVIEW.canTransitionTo(REJECTED));
        assertFalse(INTERVIEW.canTransitionTo(APPLIED));
    }

    @Test
    void finalStatesCannotChange() {
        for (ApplicationStatus next : ApplicationStatus.values()) {
            assertFalse(OFFER.canTransitionTo(next));
            assertFalse(REJECTED.canTransitionTo(next));
        }
    }
}
