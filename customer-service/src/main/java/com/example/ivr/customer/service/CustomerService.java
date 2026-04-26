package com.example.ivr.customer.service;

import com.example.ivr.customer.dto.CustomerResponse;
import com.example.ivr.customer.entity.Customer;
import com.example.ivr.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse getByPhone(String phone) {
        Customer customer = customerRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found for phone: " + phone));
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getEmail()
        );
    }
}
