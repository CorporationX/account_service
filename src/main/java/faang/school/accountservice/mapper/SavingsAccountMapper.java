package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.model.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

//    @Mapping(target = "", source = "tariffHistory", ignore = true)
//    @Mapping(target = "", source = "lastCalculatedDate", ignore = true)
//    @Mapping(target = "", source = "version", ignore = true)
//    @Mapping(target = "", source = "createdAt", ignore = true)
//    @Mapping()
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
