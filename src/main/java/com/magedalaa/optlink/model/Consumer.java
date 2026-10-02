package com.magedalaa.optlink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * A person who has opted in to be listed in the marketplace.
 *
 * consentGiven/consentGivenAt record a timestamped, provable opt-in rather
 * than an assumed one. withdrawnAt is set instead of deleting the row
 * outright, so there's still an auditable record that this person was
 * listed and chose to leave - and a company that viewed them before
 * withdrawal still shows up correctly in its access log history.
 */
@Entity
@Table(name = "consumers")
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    private String phone;

    @NotBlank
    private String city;

    /** e.g. "Retail", "Fitness", "Finance" - what kind of company offers would interest them. */
    @NotBlank
    private String interestCategory;

    private boolean consentGiven;

    private LocalDateTime consentGivenAt;

    private LocalDateTime withdrawnAt;

    protected Consumer() {
        // required by JPA
    }

    public Consumer(String fullName, String email, String phone, String city, String interestCategory) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.city = city;
        this.interestCategory = interestCategory;
    }

    public void giveConsent() {
        this.consentGiven = true;
        this.consentGivenAt = LocalDateTime.now();
        this.withdrawnAt = null;
    }

    public void withdrawConsent() {
        this.consentGiven = false;
        this.withdrawnAt = LocalDateTime.now();
    }

    public boolean isActivelyListed() {
        return consentGiven && withdrawnAt == null;
    }

    // --- getters (no setters beyond the intentional ones above - consent
    // state should only ever change through giveConsent()/withdrawConsent(),
    // never by silently overwriting a field) ---

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getInterestCategory() { return interestCategory; }
    public boolean isConsentGiven() { return consentGiven; }
    public LocalDateTime getConsentGivenAt() { return consentGivenAt; }
    public LocalDateTime getWithdrawnAt() { return withdrawnAt; }
}
