package faang.school.accountservice.service;


import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Override
    @Transactional
    public Request createRequest(Request request) {
        log.debug("Starting to create request: {}", request);
        validateRequest(request);
        if (request.getIdpToken() == null) {
            request.setIdpToken(UUID.randomUUID());
            log.debug("Generated new IDP token: {}", request.getIdpToken());
        }
        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.PENDING);
            log.debug("Set default status to PENDING");
        }
        Request savedRequest = requestRepository.save(request);
        log.info("Request created successfully: {}", savedRequest);
        return savedRequest;
    }

    @Override
    public Optional<Request> findById(UUID idpToken) {
        return requestRepository.findById(idpToken);
    }

    @Override
    @Transactional
    public void updateStatus(UUID idpToken, RequestStatus status) {
        log.debug("Updating status for request with IDP token: {}, new status: {}", idpToken, status);
        Request request = requestRepository.findById(idpToken)
                .orElseThrow(() -> {
                    log.error("Request not found with IDP token: {}", idpToken);
                    return new RuntimeException("Request not found");
                });
        log.debug("Current status: {}, new status: {}", request.getStatus(), status);
        request.setStatus(status);
        requestRepository.save(request);
        log.info("Status updated successfully for request: {}", idpToken);
    }

    @Override
    @Transactional
    public void updateContext(UUID idpToken, Map<String, Object> context) {
        log.debug("Updating context for request with IDP token: {}", idpToken);
        Request request = requestRepository.findById(idpToken)
                .orElseThrow(() -> {
                    log.error("Request not found with IDP token for updateContext: {}", idpToken);
                    return new RuntimeException("Request not found");
                });
        log.debug("Updating context with data: {}", context);
        request.setInputData(context);
        requestRepository.save(request);
        log.info("Context updated successfully for request: {}", idpToken);
    }

    private void validateRequest(Request request) {
        log.debug("Validating request: {}", request);
        if (request.getUserId() == null) {
            log.error("Validation failed: User ID is required");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        if (request.getOperationType() == null) {
            log.error("Validation failed: Operation type is required");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation type is required");
        }
        log.debug("Request validation successful");
    }
}