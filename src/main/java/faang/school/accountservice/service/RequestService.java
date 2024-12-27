package faang.school.accountservice.service;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    //check: do request tasks save together with request
    public void updateRequest(Request request) {
       requestRepository.save(request);
    }
}
