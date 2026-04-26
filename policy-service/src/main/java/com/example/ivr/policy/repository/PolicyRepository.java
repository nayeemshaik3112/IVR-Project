package com.example.ivr.policy.repository;

import com.example.ivr.policy.entity.Policy;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByCustomerId(Long customerId);
}
