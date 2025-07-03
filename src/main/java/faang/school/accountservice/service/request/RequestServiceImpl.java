package faang.school.accountservice.service.request;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.model.Request;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository repository;

    @Override
    public Request createRequest(Request request) {
        return null;
    }

    @Override
    public void updateStatus(UUID token, RequestStatus status, String description) {

    }

    @Override
    public void closeRequest(UUID token) {

    }

    @Override
    public void updateInputContext(UUID token, Map<String, Object> input) {

    }
}
