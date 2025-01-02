package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.message.event.RequestInProgressEvent;
import faang.school.accountservice.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    @Mapping(target = "idempotencyToken", expression = "java(java.util.UUID.fromString(createRequestDto.idempotencyToken()))")
    Request toRequest(CreateRequestDto createRequestDto);

    @Mapping(target = "idempotencyToken", expression = "java(request.getIdempotencyToken().toString())")
    ResponseRequestDto toResponseRequestDto(Request request);

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "receiverId", source = "userId")
    RequestInProgressEvent toRequestInProgressEvent(Request request);
}
