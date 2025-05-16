package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.message.AuthorizationMessage;
import faang.school.accountservice.model.AccountOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AccountOperationMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "paymentOperationId", source = "operationId")
    @Mapping(target = "operationType", constant = "AUTHORIZATION")
    @Mapping(target = "operationStatus", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "errorMessage", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorizationId", source = "operationId")
    AccountOperation authMessageToAccountOperation(AuthorizationMessage authorizationMessage);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "operationType", ignore = true)
    @Mapping(target = "paymentOperationId", source = "newOperationId")
    @Mapping(target = "operationStatus", constant = "COMPLETED")
    AccountOperation cloneOperation(AccountOperation operation, UUID newOperationId);
}
