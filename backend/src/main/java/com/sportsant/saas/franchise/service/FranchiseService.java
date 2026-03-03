package com.sportsant.saas.franchise.service;

import com.sportsant.saas.finance.service.FinanceService;
import com.sportsant.saas.franchise.entity.FranchiseApplication;
import com.sportsant.saas.franchise.repository.FranchiseApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FranchiseService {

    @Autowired
    private FranchiseApplicationRepository applicationRepository;

    @Autowired
    private FinanceService financeService;

    public List<FranchiseApplication> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Transactional
    public FranchiseApplication submitApplication(FranchiseApplication app) {
        if (app.getInitialFee() == null) {
            app.setInitialFee(100000.0); // Default franchise fee
        }
        app.setStatus("PENDING");
        return applicationRepository.save(app);
    }

    @Transactional
    public FranchiseApplication approveApplication(Long id) {
        FranchiseApplication app = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));
            
        app.setStatus("APPROVED");
        return applicationRepository.save(app);
    }

    @Transactional
    public FranchiseApplication payFranchiseFee(Long id) {
        FranchiseApplication app = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Application not found"));
            
        if (!"APPROVED".equals(app.getStatus())) {
            throw new RuntimeException("Application must be approved before payment");
        }

        // 1. Update Status
        app.setStatus("PAID");
        applicationRepository.save(app);

        // 2. Finance Record (Revenue Recognition)
        // In Franchise business, initial fee is often recognized immediately or amortized.
        // For MVP, we recognize it as "Other Revenue" or "Franchise Revenue"
        financeService.createVoucher(
            "FRANCHISE_FEE", 
            app.getId(), 
            BigDecimal.valueOf(app.getInitialFee()), 
            "Franchise Fee from " + app.getApplicantName(), 
            "CN"
        );

        return app;
    }
}
