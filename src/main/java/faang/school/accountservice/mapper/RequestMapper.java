package faang.school.accountservice.mapper;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RequestMapper {

    @Mapping(target = "context", source = "id")
    Request accountToRequest(Account account);
}
