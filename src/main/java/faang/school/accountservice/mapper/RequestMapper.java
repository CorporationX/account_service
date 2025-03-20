package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestReadDto;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {RequestStatus.class}
)
public interface RequestMapper {

    @Mapping(target = "authorAccountNumber", source = "author.accountNumber")
    @Mapping(target = "receiverAccountNumber", source = "receiver.accountNumber")
    @Mapping(target = "requestStatus", source = "status")
    RequestReadDto toDto(Request request);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "receiver", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(RequestStatus.WAITING)")
    @Mapping(target = "isOpen", expression = "java(true)")
    Request toEntity(RequestCreateDto dto);
}
