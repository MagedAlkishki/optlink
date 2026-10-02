package com.magedalaa.optlink.controller;

import com.magedalaa.optlink.dto.AccessLogEntryView;
import com.magedalaa.optlink.dto.CompanySignupRequest;
import com.magedalaa.optlink.model.Company;
import com.magedalaa.optlink.service.CompanyService;
import com.magedalaa.optlink.service.ConsumerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final ConsumerService consumerService;

    public CompanyController(CompanyService companyService, ConsumerService consumerService) {
        this.companyService = companyService;
        this.consumerService = consumerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Company> signUp(@Valid @RequestBody CompanySignupRequest request) {
        return ResponseEntity.status(201).body(companyService.signUp(request));
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<Company> subscribe(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.subscribe(id));
    }

    @PostMapping("/{id}/cancel-subscription")
    public ResponseEntity<Company> cancelSubscription(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.cancelSubscription(id));
    }

    /** A company's own audit trail: exactly which consumers it has revealed contact details for, and when. */
    @GetMapping("/{id}/access-log")
    public ResponseEntity<List<AccessLogEntryView>> accessLog(@PathVariable Long id) {
        // Confirms the company exists (404s otherwise) before returning its log.
        companyService.getOrThrow(id);
        return ResponseEntity.ok(consumerService.accessHistoryFor(id));
    }
}
