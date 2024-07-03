package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.transaction.TransactionDto;
import faang.school.accountservice.dto.transaction.TransactionDtoToCreate;
import faang.school.accountservice.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionMapper {

    Transaction toEntity(TransactionDtoToCreate dto);

    TransactionDto toDto(Transaction entity);
}