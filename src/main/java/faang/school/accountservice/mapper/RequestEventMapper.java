package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.entity.RequestEvent;
import faang.school.accountservice.events.RequestEventEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestEventMapper {

    @Mapping(target = "createdAt", source = "timestamp")
    RequestEvent toEntity(RequestEventDto requestEventDto);

    @Mapping(target = "id", source = "token")
    RequestEventDto toRequestEventDto(CreateRequestDto createRequestDto);

    @Mapping(target = "timestamp", source = "createdAt")
    List<RequestEventEvent> toRequestEventEventList(List<RequestEvent> requests);
}
