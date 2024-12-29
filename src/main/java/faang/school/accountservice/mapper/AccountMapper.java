package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    Account toEntity(CreateAccountDto dto);

    AccountDto toDto(Account account);

    List<AccountDto> toDto(List<Account> accounts);

    @Mapping(source = "updatedAt", target = "transactionDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    TransactionDto toDto(Transaction transaction);
}
