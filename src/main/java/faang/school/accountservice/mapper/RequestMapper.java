package faang.school.accountservice.mapper;

import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.dto.RequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    @Mapping(target = "requestType", source = "requestType")
    @Mapping(target = "status", source = "status")
    RequestDto toDto(Request request);

    List<RequestDto> toDtoList(List<Request> requests);

    Request toEntity(RequestDto dto);
}