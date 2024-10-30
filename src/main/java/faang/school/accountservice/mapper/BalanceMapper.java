package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@RequiredArgsConstructor
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class BalanceMapper {
    protected AccountRepository repository;

    @Mapping(target = "account", source = "accountId", qualifiedByName = "getAccount")
    public abstract Balance toEntity(BalanceDto dto);

    @Mapping(target = "accountId", source = "account.id")
    public abstract BalanceDto toDto(Balance balance);

    @Named(value = "getAccount")
    public Account getAccount(long id) {
        return repository.getReferenceById(id);
    }
}
