package com.example.ivr.session.service;

import com.example.ivr.session.dto.IvrSessionResponse;
import com.example.ivr.session.dto.IvrSessionStartRequest;
import com.example.ivr.session.dto.IvrSessionUpdateRequest;
import com.example.ivr.session.entity.IvrSession;
import com.example.ivr.session.entity.SessionStatus;
import com.example.ivr.session.exception.IvrSessionNotFoundException;
import com.example.ivr.session.repository.IvrSessionRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IvrSessionService {

    private final IvrSessionRepository repository;

    public IvrSessionService(IvrSessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IvrSessionResponse openOrResume(IvrSessionStartRequest request) {
        IvrSession session = repository.findByRequestId(request.requestId()).orElseGet(IvrSession::new);
        Instant now = Instant.now();
        if (session.getId() == null) {
            session.setRequestId(request.requestId());
            session.setStartedAt(now);
            session.setStatus(SessionStatus.OPEN);
        }
        session.setPhone(request.phone());
        session.setIntent(request.intent());
        session.setLastUpdatedAt(now);
        return map(repository.save(session));
    }

    @Transactional
    public IvrSessionResponse update(String requestId, IvrSessionUpdateRequest request) {
        IvrSession session = repository.findByRequestId(requestId)
                .orElseThrow(() -> new IvrSessionNotFoundException(requestId));
        session.setCustomerId(request.customerId());
        session.setSelectedPolicyNumber(request.selectedPolicyNumber());
        session.setLastSummary(request.lastSummary());
        session.setLastUpdatedAt(Instant.now());
        return map(repository.save(session));
    }

    @Transactional
    public IvrSessionResponse complete(String requestId, String summary) {
        IvrSession session = repository.findByRequestId(requestId)
                .orElseThrow(() -> new IvrSessionNotFoundException(requestId));
        session.setStatus(SessionStatus.COMPLETED);
        session.setLastSummary(summary);
        session.setLastUpdatedAt(Instant.now());
        session.setEndedAt(Instant.now());
        return map(repository.save(session));
    }

    @Transactional
    public IvrSessionResponse fail(String requestId, String summary) {
        IvrSession session = repository.findByRequestId(requestId)
                .orElseThrow(() -> new IvrSessionNotFoundException(requestId));
        session.setStatus(SessionStatus.FAILED);
        session.setLastSummary(summary);
        session.setLastUpdatedAt(Instant.now());
        session.setEndedAt(Instant.now());
        return map(repository.save(session));
    }

    @Transactional(readOnly = true)
    public IvrSessionResponse get(String requestId) {
        return repository.findByRequestId(requestId)
                .map(this::map)
                .orElseThrow(() -> new IvrSessionNotFoundException(requestId));
    }

    private IvrSessionResponse map(IvrSession session) {
        return new IvrSessionResponse(
                session.getRequestId(),
                session.getPhone(),
                session.getCustomerId(),
                session.getSelectedPolicyNumber(),
                session.getIntent(),
                session.getLastSummary(),
                session.getStatus().name(),
                session.getStartedAt(),
                session.getLastUpdatedAt(),
                session.getEndedAt()
        );
    }
}