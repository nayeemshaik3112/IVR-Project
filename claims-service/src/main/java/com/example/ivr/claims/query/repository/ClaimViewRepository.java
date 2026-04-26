package com.example.ivr.claims.query.repository;

import com.example.ivr.claims.query.entity.ClaimView;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimViewRepository extends JpaRepository<ClaimView, String> {

    List<ClaimView> findByCustomerIdOrderByUpdatedAtDesc(Long customerId);
}
