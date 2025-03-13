package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {
    SavingsAccount toProjectEntity(SavingsAccountDto dto);

    SavingsAccountDto toSavingsAccountResponseDto(SavingsAccount entity);

    List<SavingsAccountDto> toSavingsAccountResponseDtos(List<SavingsAccount> entities);
}
