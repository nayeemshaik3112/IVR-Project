package com.example.ivr.orchestrator.controller;

import com.example.ivr.orchestrator.dto.IvrAggregatedResponse;
import com.example.ivr.orchestrator.service.IvrAggregationService;
import java.util.concurrent.CompletableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ivr")
public class IvrController {

    private final IvrAggregationService ivrAggregationService;

    public IvrController(IvrAggregationService ivrAggregationService) {
        this.ivrAggregationService = ivrAggregationService;
    }

    @GetMapping("/respond")
    public CompletableFuture<IvrAggregatedResponse> respond(@RequestParam String phone,
                                                            @RequestHeader(value = "X-IVR-Request-ID", required = false)
                                                            String requestId) {
        return ivrAggregationService.aggregate(phone, requestId);
    }
}
