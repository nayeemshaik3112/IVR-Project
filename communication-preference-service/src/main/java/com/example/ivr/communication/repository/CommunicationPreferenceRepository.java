package com.example.ivr.communication.repository;

import com.example.ivr.communication.entity.CommunicationPreference;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunicationPreferenceRepository extends JpaRepository<CommunicationPreference, Long> {

    Optional<CommunicationPreference> findByCustomerId(Long customerId);
}