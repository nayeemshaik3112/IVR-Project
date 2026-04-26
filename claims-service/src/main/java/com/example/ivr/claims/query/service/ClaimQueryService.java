package com.example.ivr.claims.query.service;

import com.example.ivr.claims.dto.ClaimResponse;
import com.example.ivr.claims.query.entity.ClaimView;
import com.example.ivr.claims.query.repository.ClaimViewRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClaimQueryService {

    private final ClaimViewRepository claimViewRepository;

    public ClaimQueryService(ClaimViewRepository claimViewRepository) {
        this.claimViewRepository = claimViewRepository;
    }

    public List<ClaimResponse> getClaimsByCustomerId(Long customerId) {
        return claimViewRepository.findByCustomerIdOrderByUpdatedAtDesc(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ClaimResponse getClaim(String claimId) {
        return claimViewRepository.findById(claimId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimId));
    }

    private ClaimResponse toResponse(ClaimView claim) {
        return new ClaimResponse(
                claim.getClaimId(),
                claim.getCustomerId(),
                claim.getPolicyNumber(),
                claim.getStatus(),
                claim.getDescription(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }
}
