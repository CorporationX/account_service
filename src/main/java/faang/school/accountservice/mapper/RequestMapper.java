package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    Request toEntity(RequestDto dto);

    RequestDto toDto(Request entity);

    void updateEntityFromDto(RequestDto dto, @MappingTarget Request entity);

    @Mapping(target = "inputData", ignore = true)
    @Mapping(target = "id", ignore = true)
    RequestDto toEvent(RequestDto requestDto);

    @Mapping(target = "context", source = "id")
    Request accountToRequest(Account account);
}
