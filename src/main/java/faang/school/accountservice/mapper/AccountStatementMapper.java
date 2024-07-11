package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account_statement.dgf;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountStatementMapper {

    dgf toDto(Resource resource);
}