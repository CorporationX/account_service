package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.FreeAccountNumberDto;
import faang.school.accountservice.model.FreeAccountNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FreeAccountNumberMapper {

    @Mapping(target = "type", source = "id.accountType")
    @Mapping(target = "number", source = "id.number")
    FreeAccountNumberDto toAccountNumberDto(FreeAccountNumber freeAccountNumber);
}
