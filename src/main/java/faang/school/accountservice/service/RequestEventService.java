package faang.school.accountservice.service;

import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.events.RequestEventEvent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RequestEventService {

    void create(RequestEventDto requestEventDto);
    void deleteAllEventsByIds(Collection<UUID> requestEventIds);
    List<RequestEventEvent> getEventsSortedByCreationDate(int maxItemsCount);
}
