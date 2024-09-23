package faang.school.accountservice.mapper.request;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    Request toEntity(RequestDto dto);

    List<RequestDto> toDtos(List<Request> entities);
}
