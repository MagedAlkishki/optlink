package com.magedalaa.optlink.controller;

import com.magedalaa.optlink.dto.ConsumerSearchResult;
import com.magedalaa.optlink.dto.ConsumerSignupRequest;
import com.magedalaa.optlink.model.Company;
import com.magedalaa.optlink.model.Consumer;
import com.magedalaa.optlink.service.CompanyService;
import com.magedalaa.optlink.service.ConsumerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    private final ConsumerService consumerService;
    private final CompanyService companyService;

    public ConsumerController(ConsumerService consumerService, CompanyService companyService) {
        this.consumerService = consumerService;
        this.companyService = companyService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Consumer> signUp(@Valid @RequestBody ConsumerSignupRequest request) {
        Consumer created = consumerService.signUp(request);
        return ResponseEntity.status(201).body(created);
    }

    /** The "remove my data" action. */
    @DeleteMapping("/{id}/consent")
    public ResponseEntity<Void> withdrawConsent(@PathVariable Long id) {
        consumerService.withdrawConsent(id);
        return ResponseEntity.noContent().build();
    }

    // companyId is a query param instead of coming from an authenticated
    // session - no auth layer in this demo, kept out of scope on purpose.
    @GetMapping("/search")
    public ResponseEntity<List<ConsumerSearchResult>> search(
            @RequestParam(required = false) String interestCategory,
            @RequestParam Long companyId) {
        Company company = companyService.getOrThrow(companyId);
        return ResponseEntity.ok(consumerService.search(interestCategory, company));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> activeCount() {
        return ResponseEntity.ok(consumerService.activeConsumerCount());
    }
}
