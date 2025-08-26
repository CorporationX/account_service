package faang.school.accountservice.service;

import faang.school.accountservice.entity.account.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.repository.RequestRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class RequestServiceImp implements RequestService{

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private RequestScheduler requestScheduler;

    @Override
    public void createRequest(Request request) {
        // Сохраняем запрос
        Request savedRequest = requestRepository.save(request);

        // Планируем отправку уведомления
        requestScheduler.scheduleNotification(savedRequest.getId());
    }

    @Override
    public Request updateStatus(Long id, RequestStatus newStatus) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setStatus(newStatus);
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public Request updateFlag(Long id, boolean newFlag) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setFlag(newFlag);
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public Request updateContext(Long id, String newContext) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setContext(newContext);
        request.setUpdatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    @Override
    public Request getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
    }
}
