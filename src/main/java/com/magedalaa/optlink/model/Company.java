package com.magedalaa.optlink.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * A business account. subscriptionActive gates how much of a Consumer's
 * data this company can see - see ConsumerService.toSearchResult() for
 * where that's actually enforced. There's no real payment processor wired
 * up here (this is a demo), so "subscribing" just flips this flag - the
 * point of the project is showing the access-control/consent architecture,
 * not building a billing system.
 */
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String contactEmail;

    private boolean subscriptionActive;

    private LocalDateTime subscribedAt;

    protected Company() {
    }

    public Company(String name, String contactEmail) {
        this.name = name;
        this.contactEmail = contactEmail;
    }

    public void activateSubscription() {
        this.subscriptionActive = true;
        this.subscribedAt = LocalDateTime.now();
    }

    public void cancelSubscription() {
        this.subscriptionActive = false;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getContactEmail() { return contactEmail; }
    public boolean isSubscriptionActive() { return subscriptionActive; }
    public LocalDateTime getSubscribedAt() { return subscribedAt; }
}
