package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.balance.BalanceCreateResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateRequestDto;
import faang.school.accountservice.dto.balance.BalanceUpdateResponseDto;
import faang.school.accountservice.entity.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BalanceMapper {

    @Mapping(target = "accountNumber", source = "account.accountNumber")
    BalanceCreateResponseDto toBalanceCreateResponseDto(Balance balance);

    void updateBalance(BalanceUpdateRequestDto balanceUpdateRequestDto, @MappingTarget Balance balance);

    @Mapping(target = "accountNumber", source = "account.accountNumber")
    BalanceUpdateResponseDto toBalanceUpdateResponseDto(Balance balance);
}
