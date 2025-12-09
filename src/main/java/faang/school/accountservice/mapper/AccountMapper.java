package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "number", source = "number")
    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "ownerType", source = "ownerType")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "closedAt", source = "closedAt")
    @Mapping(target = "version", source = "version")
    AccountResponseDto toDto(Account account);

    default Page<AccountResponseDto> toPageDto(Page<Account> accounts) {
        return accounts.map(this::toDto);
    }
}

