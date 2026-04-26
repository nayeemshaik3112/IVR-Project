package com.example.ivr.customer.controller;

import com.example.ivr.customer.dto.CustomerResponse;
import com.example.ivr.customer.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/search")
    public CustomerResponse searchByPhone(@RequestParam String phone) {
        return customerService.getByPhone(phone);
    }
}
