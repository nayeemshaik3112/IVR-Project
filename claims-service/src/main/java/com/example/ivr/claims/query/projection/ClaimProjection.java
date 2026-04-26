package com.example.ivr.claims.query.projection;

import com.example.ivr.claims.command.event.ClaimCreatedEvent;
import com.example.ivr.claims.command.event.ClaimStatusUpdatedEvent;
import com.example.ivr.claims.query.entity.ClaimView;
import com.example.ivr.claims.query.repository.ClaimViewRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class ClaimProjection {

    private final ClaimViewRepository claimViewRepository;

    public ClaimProjection(ClaimViewRepository claimViewRepository) {
        this.claimViewRepository = claimViewRepository;
    }

    @EventHandler
    public void on(ClaimCreatedEvent event) {
        ClaimView claimView = new ClaimView();
        claimView.setClaimId(event.getClaimId());
        claimView.setCustomerId(event.getCustomerId());
        claimView.setPolicyNumber(event.getPolicyNumber());
        claimView.setDescription(event.getDescription());
        claimView.setStatus(event.getStatus());
        claimView.setCreatedAt(event.getOccurredAt());
        claimView.setUpdatedAt(event.getOccurredAt());
        claimViewRepository.save(claimView);
    }

    @EventHandler
    public void on(ClaimStatusUpdatedEvent event) {
        claimViewRepository.findById(event.getClaimId()).ifPresent(claimView -> {
            claimView.setStatus(event.getStatus());
            claimView.setUpdatedAt(event.getOccurredAt());
            claimViewRepository.save(claimView);
        });
    }
}
