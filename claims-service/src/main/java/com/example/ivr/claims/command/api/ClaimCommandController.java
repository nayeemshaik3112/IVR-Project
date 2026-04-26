package com.example.ivr.claims.command.api;

import com.example.ivr.claims.command.command.CreateClaimCommand;
import com.example.ivr.claims.command.command.UpdateClaimStatusCommand;
import com.example.ivr.claims.dto.ClaimRequest;
import com.example.ivr.claims.dto.ClaimStatusUpdateRequest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/claims")
public class ClaimCommandController {

    private final CommandGateway commandGateway;

    public ClaimCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Map<String, String>> createClaim(@RequestBody ClaimRequest request) {
        String claimId = UUID.randomUUID().toString();
        return commandGateway.send(new CreateClaimCommand(
                        claimId,
                        request.customerId(),
                        request.policyNumber(),
                        request.description()))
                .thenApply(result -> Map.of("claimId", claimId, "message", "Claim creation accepted"));
    }

    @PutMapping("/{claimId}/status")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CompletableFuture<Map<String, String>> updateStatus(@PathVariable String claimId,
                                                               @RequestBody ClaimStatusUpdateRequest request) {
        return commandGateway.send(new UpdateClaimStatusCommand(claimId, request.status()))
                .thenApply(result -> Map.of("claimId", claimId, "message", "Claim status update accepted"));
    }
}
