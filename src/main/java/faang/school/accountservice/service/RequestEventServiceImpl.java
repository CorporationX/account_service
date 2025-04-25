package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.enums.RequestVersion;
import faang.school.accountservice.mapper.RequestEventMapper;
import faang.school.accountservice.repository.RequestEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestEventServiceImpl implements RequestEventService {
    private final RequestEventRepository requestEventRepository;
    private final RequestEventMapper requestEventMapper;

    public void create(RequestEventDto requestEventDto) {
        var requestEvent = requestEventMapper.toEntity(requestEventDto);
        requestEvent.setRequestVersion(RequestVersion.V1);

        requestEventRepository.save(requestEvent);
    }

    public void deleteById(UUID requestEventId) {
        requestEventRepository.deleteById(requestEventId);
    }
}
