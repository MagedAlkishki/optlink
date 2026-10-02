package com.magedalaa.optlink.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * One row per "a company viewed a consumer's full contact details" -
 * written only when real details are revealed, never on a masked search
 * result. Answers the question a regulator (or the consumer) would ask
 * first: who looked at my data, and when.
 */
@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(optional = false)
    @JoinColumn(name = "consumer_id")
    private Consumer consumer;

    private LocalDateTime accessedAt;

    protected AccessLog() {
    }

    public AccessLog(Company company, Consumer consumer) {
        this.company = company;
        this.consumer = consumer;
        this.accessedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Company getCompany() { return company; }
    public Consumer getConsumer() { return consumer; }
    public LocalDateTime getAccessedAt() { return accessedAt; }
}
