package com.magedalaa.optlink.service;

import com.magedalaa.optlink.dto.AccessLogEntryView;
import com.magedalaa.optlink.dto.ConsumerSearchResult;
import com.magedalaa.optlink.dto.ConsumerSignupRequest;
import com.magedalaa.optlink.model.AccessLog;
import com.magedalaa.optlink.model.Company;
import com.magedalaa.optlink.model.Consumer;
import com.magedalaa.optlink.repository.AccessLogRepository;
import com.magedalaa.optlink.repository.ConsumerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ConsumerService {

    private final ConsumerRepository consumerRepository;
    private final AccessLogRepository accessLogRepository;

    public ConsumerService(ConsumerRepository consumerRepository, AccessLogRepository accessLogRepository) {
        this.consumerRepository = consumerRepository;
        this.accessLogRepository = accessLogRepository;
    }

    // giveConsent() is what actually stamps consentGivenAt - the
    // provable "when did they agree" record.
    public Consumer signUp(ConsumerSignupRequest request) {
        Consumer consumer = new Consumer(
                request.fullName(),
                request.email(),
                request.phone(),
                request.city(),
                request.interestCategory()
        );
        consumer.giveConsent();
        return consumerRepository.save(consumer);
    }

    /** Withdraws consent (soft - see Consumer.withdrawConsent()) rather than deleting the row outright. */
    public void withdrawConsent(Long consumerId) {
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new NoSuchElementException("No consumer with id " + consumerId));
        consumer.withdrawConsent();
        consumerRepository.save(consumer);
    }

    // Search, gated by the requesting company's subscription status: every
    // company can browse masked matches for free, but only a subscribed
    // one gets real contact info - and only then is an AccessLog written.
    public List<ConsumerSearchResult> search(String interestCategory, Company requestingCompany) {
        List<Consumer> matches = (interestCategory == null || interestCategory.isBlank())
                ? consumerRepository.findByConsentGivenTrueAndWithdrawnAtIsNull()
                : consumerRepository.findByConsentGivenTrueAndWithdrawnAtIsNullAndInterestCategoryIgnoreCase(interestCategory);

        return matches.stream()
                .map(consumer -> toSearchResult(consumer, requestingCompany))
                .toList();
    }

    private ConsumerSearchResult toSearchResult(Consumer consumer, Company requestingCompany) {
        boolean reveal = requestingCompany.isSubscriptionActive();

        if (reveal) {
            accessLogRepository.save(new AccessLog(requestingCompany, consumer));
            return new ConsumerSearchResult(
                    consumer.getId(),
                    consumer.getFullName(),
                    consumer.getCity(),
                    consumer.getInterestCategory(),
                    true,
                    consumer.getEmail(),
                    consumer.getPhone()
            );
        }

        return new ConsumerSearchResult(
                consumer.getId(),
                maskName(consumer.getFullName()),
                consumer.getCity(),
                consumer.getInterestCategory(),
                false,
                null,
                null
        );
    }

    /** "Maged Alaa" -> "M**** A***" - enough to see a match exists, not enough to identify or contact them. */
    private String maskName(String fullName) {
        return java.util.Arrays.stream(fullName.trim().split("\\s+"))
                .map(part -> part.isEmpty() ? part : part.charAt(0) + "*".repeat(Math.max(part.length() - 1, 1)))
                .reduce((a, b) -> a + " " + b)
                .orElse("****");
    }

    public List<AccessLogEntryView> accessHistoryFor(Long companyId) {
        return accessLogRepository.findByCompanyIdOrderByAccessedAtDesc(companyId).stream()
                .map(log -> new AccessLogEntryView(
                        log.getConsumer().getId(),
                        log.getConsumer().getFullName(),
                        log.getAccessedAt()
                ))
                .toList();
    }

    public long activeConsumerCount() {
        return consumerRepository.findByConsentGivenTrueAndWithdrawnAtIsNull().size();
    }
}
