package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.RequestResponseDto;
import faang.school.accountservice.model.Request;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "open", target = "isOpen")
    RequestResponseDto toDto(Request request);
}
