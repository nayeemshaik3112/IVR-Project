package com.example.ivr.session.repository;

import com.example.ivr.session.entity.IvrSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IvrSessionRepository extends JpaRepository<IvrSession, Long> {

    Optional<IvrSession> findByRequestId(String requestId);
}