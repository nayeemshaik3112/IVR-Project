package com.example.ivr.orchestrator.repository;

import com.example.ivr.orchestrator.entity.IvrRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IvrRequestLogRepository extends JpaRepository<IvrRequestLog, String> {
}
