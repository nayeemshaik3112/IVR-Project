package com.example.ivr.orchestrator.client;

import com.example.ivr.orchestrator.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-service")
public interface CustomerClient {

    @GetMapping("/api/customers/search")
    CustomerResponse getCustomerByPhone(@RequestParam("phone") String phone);
}
