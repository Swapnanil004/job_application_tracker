package com.swapnanil.jobtracker.entity;

/**
 * Status workflow:
 * APPLIED -> INTERVIEW -> OFFER, and REJECTED is possible from APPLIED or INTERVIEW.
 * OFFER and REJECTED are final states.
 */
public enum ApplicationStatus {
    APPLIED,
    INTERVIEW,
    OFFER,
    REJECTED;

    public boolean canTransitionTo(ApplicationStatus next) {
        return switch (this) {
            case APPLIED -> next == INTERVIEW || next == REJECTED;
            case INTERVIEW -> next == OFFER || next == REJECTED;
            case OFFER, REJECTED -> false;
        };
    }
}
