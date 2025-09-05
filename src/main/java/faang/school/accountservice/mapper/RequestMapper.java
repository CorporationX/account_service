package faang.school.accountservice.mapper;

import faang.school.accountservice.model.Request;
import faang.school.accountservice.model.dto.RequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    RequestDto toDto(Request request);

    List<RequestDto> toDtoList(List<Request> requests);

    Request toEntity(RequestDto dto);
}