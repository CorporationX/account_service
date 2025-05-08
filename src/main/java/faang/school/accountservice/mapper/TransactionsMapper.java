package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionsMapper {
    TransactionDto toDto(Transaction transaction);

    Transaction toEntity(TransactionDto transactionDto);

}

