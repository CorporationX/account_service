package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savings.SavingsAccountResponseDto;
import faang.school.accountservice.model.savings.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SavingsAccountMapper {

  @Mapping(target = "tariff", source = "tariffHistory", ignore = true)
  SavingsAccountResponseDto toDto(SavingsAccount account);

}
