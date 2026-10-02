package com.magedalaa.optlink.service;

import com.magedalaa.optlink.dto.CompanySignupRequest;
import com.magedalaa.optlink.model.Company;
import com.magedalaa.optlink.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company signUp(CompanySignupRequest request) {
        Company company = new Company(request.name(), request.contactEmail());
        return companyRepository.save(company);
    }

    /** No real payment processor - this is a demo, so "subscribing" just flips the flag. */
    public Company subscribe(Long companyId) {
        Company company = getOrThrow(companyId);
        company.activateSubscription();
        return companyRepository.save(company);
    }

    public Company cancelSubscription(Long companyId) {
        Company company = getOrThrow(companyId);
        company.cancelSubscription();
        return companyRepository.save(company);
    }

    public Company getOrThrow(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("No company with id " + companyId));
    }
}
