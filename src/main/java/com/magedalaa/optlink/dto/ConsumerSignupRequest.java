package com.magedalaa.optlink.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Shape of the consumer sign-up request body.
 *
 * consentGiven is @AssertTrue rather than a plain boolean, so a request
 * with the box unchecked (or missing) gets rejected with a 400 before it
 * ever reaches signUp() - there's no code path that creates a Consumer
 * without consent.
 */
public record ConsumerSignupRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String phone,
        @NotBlank String city,
        @NotBlank String interestCategory,
        @AssertTrue(message = "Consent must be explicitly given to sign up") boolean consentGiven
) {
}
