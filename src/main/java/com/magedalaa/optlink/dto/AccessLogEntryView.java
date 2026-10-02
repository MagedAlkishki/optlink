package com.magedalaa.optlink.dto;

import java.time.LocalDateTime;

/** One line of a company's audit trail: which consumer they viewed, and when. */
public record AccessLogEntryView(
        Long consumerId,
        String consumerDisplayName,
        LocalDateTime accessedAt
) {
}
