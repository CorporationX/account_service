package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestGetDto;
import faang.school.accountservice.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    //    @Mapping(source = "request.inputParams", target = "inputParams", qualifiedBy = )
    RequestGetDto toDto(Request entity);

    Request toEntity(RequestCreateDto requestCreateDto);
}
