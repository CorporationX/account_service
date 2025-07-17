package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savingAccount.ResponseSavingDto;
import faang.school.accountservice.dto.savingAccount.SavingCreateDto;
import faang.school.accountservice.entity.SavingAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingAccountMapper {

    SavingAccount toSavingAccount(SavingCreateDto dto);

    ResponseSavingDto toResponseSavingDto(SavingAccount entity);
}
