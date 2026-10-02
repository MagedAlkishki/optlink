package com.magedalaa.optlink.dto;

/**
 * What a company sees when browsing the marketplace. contactRevealed tells
 * the frontend whether email/phone are real values or null - see
 * ConsumerService.toSearchResult() for where that decision is actually
 * made (never trust the frontend to enforce this on its own; a company
 * could just call the API directly, so the gate has to live server-side).
 */
public record ConsumerSearchResult(
        Long id,
        String displayName,
        String city,
        String interestCategory,
        boolean contactRevealed,
        String email,
        String phone
) {
}
