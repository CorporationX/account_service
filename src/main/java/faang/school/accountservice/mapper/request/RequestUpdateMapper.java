package faang.school.accountservice.mapper.request;

import faang.school.accountservice.dto.request.RequestUpdateDto;
import faang.school.accountservice.entity.Request;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RequestUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "requestStatus", target = "status")
    @Mapping(source = "newStatusDetails", target = "statusDetails")
    @Mapping(source = "isOpen", target = "open")
    void updateRequestFromDto(RequestUpdateDto updateDto, @MappingTarget Request request);
}
