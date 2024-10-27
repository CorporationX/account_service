package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.impl.account.AccountServiceImpl;
import faang.school.accountservice.service.impl.account.number.FreeAccountNumbersServiceImpl;
import faang.school.accountservice.validator.AccountValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    FreeAccountNumbersServiceImpl freeAccountNumbersService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        account = Account.builder()
                .id(1L)
                .accountNumber("123456789012")
                .ownerId(1L)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        accountDto = AccountDto.builder()
                .id(1L)
                .ownerId(1L)
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    void getAllAccountsByOwnerId_shouldReturnAccountDtoList() {
        // given
        when(accountRepository.findAllByOwnerId(1L)).thenReturn(List.of(account));
        when(accountMapper.toDtoList(anyList())).thenReturn(List.of(accountDto));
        // when
        List<AccountDto> result = accountService.getAllAccountsByOwnerId(1L);
        // then
        assertThat(result).isNotEmpty();
        verify(accountRepository, times(1)).findAllByOwnerId(1L);
        verify(accountMapper, times(1)).toDtoList(anyList());
    }

    @Test
    void getAccountById_shouldReturnAccountDto() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(any(Account.class))).thenReturn(accountDto);
        // when
        AccountDto result = accountService.getAccountById(1L);
        // then
        assertThat(result).isEqualTo(accountDto);
        verify(accountRepository, times(1)).findById(1L);
        verify(accountMapper, times(1)).toDto(account);
    }

    @Test
    void getAccountById_shouldThrowEntityNotFoundException() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> accountService.getAccountById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Account with ID: 1 not found");
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void blockAccount_shouldUpdateStatusToBlocked() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        // when
        accountService.blockAccount(1L, 1L);
        // then
        assertThat(account.getAccountStatus()).isEqualTo(AccountStatus.BLOCKED);
        verify(accountValidator, times(1)).validateAccountToBlock(account, 1L);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void closeAccount_shouldUpdateStatusToClosed() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        // when
        accountService.closeAccount(1L, 1L);
        // then
        assertThat(account.getAccountStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isNotNull();
        verify(accountValidator, times(1)).validateAccountToClose(account, 1L);
        verify(accountRepository, times(1)).save(account);
    }
}
