package com.example.ivr.eligibility.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "eligibility_record")
public class EligibilityRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private String planCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageStatus coverageStatus;

    @Column(nullable = false)
    private LocalDate effectiveDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal deductibleRemaining;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal copayPercentage;

    @Column(nullable = false)
    private String networkCode;

    @Column(nullable = false)
    private Instant lastVerifiedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public CoverageStatus getCoverageStatus() {
        return coverageStatus;
    }

    public void setCoverageStatus(CoverageStatus coverageStatus) {
        this.coverageStatus = coverageStatus;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getDeductibleRemaining() {
        return deductibleRemaining;
    }

    public void setDeductibleRemaining(BigDecimal deductibleRemaining) {
        this.deductibleRemaining = deductibleRemaining;
    }

    public BigDecimal getCopayPercentage() {
        return copayPercentage;
    }

    public void setCopayPercentage(BigDecimal copayPercentage) {
        this.copayPercentage = copayPercentage;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public Instant getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(Instant lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }
}