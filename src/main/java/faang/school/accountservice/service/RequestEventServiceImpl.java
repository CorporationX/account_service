package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.enums.RequestVersion;
import faang.school.accountservice.events.RequestEventEvent;
import faang.school.accountservice.mapper.RequestEventMapper;
import faang.school.accountservice.repository.RequestEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class RequestEventServiceImpl implements RequestEventService {

    private final RequestEventRepository requestEventRepository;
    private final RequestEventMapper requestEventMapper;

    public void create(RequestEventDto requestEventDto) {
        var requestEvent = requestEventMapper.toEntity(requestEventDto);
        requestEvent.setRequestVersion(RequestVersion.V1);

        requestEventRepository.save(requestEvent);

        log.info("Request event {} for user #{} is created at {}. Type: {}. Block value: {}. Status: {}. Details: {}",
                requestEvent.getId(), requestEvent.getUserId(), requestEvent.getCreatedAt(),
                requestEvent.getRequestType(), requestEvent.getBlockValue(), requestEvent.getRequestStatus(),
                requestEvent.getDetails());
    }

    public void deleteAllEventsByIds(Collection<UUID> requestEventIds) {
        requestEventRepository.deleteAllByIds(requestEventIds);

        log.info("Request events with ids {} have been deleted",
                String.join(", ", requestEventIds.stream().map(UUID::toString).toList()));
    }

    public List<RequestEventEvent> getEventsSortedByCreationDate(int maxItemsCount) {
        var pageRequest = PageRequest.of(0, maxItemsCount);
        
        return requestEventMapper.toRequestEventEventList(
                requestEventRepository.findByOrderByCreatedAtDesc(pageRequest));
    }
}
