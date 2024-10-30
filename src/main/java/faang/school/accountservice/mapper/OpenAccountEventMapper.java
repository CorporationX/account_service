package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.OpenAccountEvent;

public interface OpenAccountEventMapper {

    OpenAccountEvent toEntity(AccountDto account);
}
