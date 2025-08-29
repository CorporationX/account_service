package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.entity.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * AccountMapper — для преобразования между сущностью {@link Account} и DTO.
 * <p>
 * Представляет методы для конвертации данных.
 * </p>*
 *
 * @author mrnght
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountMapper {

    /**
     * Преобразует DTO открытия счета в сущность {@link Account}.
     */
    Account toEntity(AccountCreateDto createDto);

    /**
     * Преобразует сущность {@link Account} в DTO для отображения.
     */
    AccountViewDto toViewDto(Account account);
}
