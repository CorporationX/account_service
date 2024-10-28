package faang.school.accountservice.mapper.type;

import faang.school.accountservice.dto.type.TypeDto;
import faang.school.accountservice.entity.type.AccountType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TypeMapper {

    TypeDto toTypeDto(AccountType accountType);

    AccountType toEntity(TypeDto typeDto);
}
