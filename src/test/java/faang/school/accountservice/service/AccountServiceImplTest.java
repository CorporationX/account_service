package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountUpdateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.AccountStatus;
import faang.school.accountservice.entity.account.AccountType;
import faang.school.accountservice.entity.account.OwnerType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.AccountMapperImpl;
import faang.school.accountservice.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceImplTest {
    @Spy
    private AccountMapperImpl mapper;
    @Mock
    private AccountRepository repository;
    @InjectMocks
    private AccountServiceImpl service;

    @Test
    @DisplayName("Проверка успешного открытия счета")
    void openAccountTest() {
        AccountCreateDto createDto = new AccountCreateDto(OwnerType.USER, 1L,
                AccountType.CURRENT_ACCOUNT, Currency.USD);
        service.openAccount(createDto);

        Account account = mapper.toEntity(createDto);
        account.setStatus(AccountStatus.ACTIVE);

        verify(repository).save(refEq(account, "accountNumber"));
    }

    @Test
    @DisplayName("Проверка изменения статуса счета")
    void changeAccountStatusTest() {
        Account account = Account.builder()
                .status(AccountStatus.ACTIVE).build();
        AccountUpdateDto updateDto = new AccountUpdateDto(AccountStatus.FROZEN);

        when(repository.findByIdOrThrow(1L)).thenReturn(account);

        AccountViewDto actualViewDto = service.changeAccountStatus(1L, updateDto);

        account.setStatus(AccountStatus.FROZEN);
        AccountViewDto expectedViewDto = mapper.toViewDto(account);

        assertEquals(expectedViewDto, actualViewDto);
    }
}
