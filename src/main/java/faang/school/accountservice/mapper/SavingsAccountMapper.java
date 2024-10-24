package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.SavingsAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {TariffHistoryMapper.class})
public interface SavingsAccountMapper {

    @Mapping(source = "account", target = "account.id")
    @Mapping(source = "holderUserId", target = "account.holderUserId")
    @Mapping(source = "tariffHistory", target = "tariffHistoryDto")
    SavingsAccountDto toDto(SavingsAccount savingsAccount);

    @Mapping(source = "account.id", target = "account")
    @Mapping(source = "tariffHistoryDto", target = "tariffHistory")
    @Mapping(source = "account.holderUserId", target = "holderUserId")
    SavingsAccount toEntity(SavingsAccountDto savingsAccountDto);
}
