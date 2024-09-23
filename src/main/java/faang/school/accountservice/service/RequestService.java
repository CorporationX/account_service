package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.mapper.request.RequestMapper;
import faang.school.accountservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public void createRequest(RequestDto requestDto) {
        requestRepository.save(requestMapper.toEntity(requestDto));
    }

    public boolean existsById(UUID id) {
        return requestRepository.existsById(id);
    }

    public boolean existsByUserLock(Long userId, Long lockUser) {
        return requestRepository.existsByUserIdAndLockUser(userId, lockUser);
    }

    @Transactional
    public void lockUser(UUID id) {
        Request request = requestRepository.getReferenceById(id);
        request.setLockUser(request.getUserId());
        request.setLockRequest(true);
        requestRepository.save(request);
    }

    @Transactional
    public void unlockUser(UUID id) {
        Request request = requestRepository.getReferenceById(id);
        request.setLockUser(null);
        request.setLockRequest(false);
        requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequests(Long userId) {
        return requestMapper.toDtos(requestRepository.getAllByUserId(userId));
    }
}
