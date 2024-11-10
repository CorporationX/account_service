package faang.school.accountservice.mapper;


import faang.school.accountservice.dto.OpenAccountEvent;
import faang.school.accountservice.dto.account.AccountDto;
import org.mapstruct.Mapper;

@Mapper
public interface OpenAccountEventMapper {

    OpenAccountEvent toEntity(AccountDto account);
}
