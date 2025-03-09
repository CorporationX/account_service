package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.Account;
import faang.school.accountservice.dto.balance.BalanceCreateRequestDto;
import faang.school.accountservice.dto.balance.BalanceCreateResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateRequestDto;
import faang.school.accountservice.dto.balance.BalanceUpdateResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private BalanceRepository balanceRepository;

    @Spy
    private BalanceMapper balanceMapper = Mappers.getMapper(BalanceMapper.class);

    @InjectMocks
    private BalanceService balanceService;

    private BalanceCreateRequestDto createRequestDto;
    private BalanceUpdateRequestDto updateRequestDto;
    private Account account;
    private Balance balance;
    private final Long balanceId = 1L;

    @BeforeEach
    void setUp() {
        createRequestDto = BalanceCreateRequestDto.builder()
                .accountNumber("ACC123")
                .build();

        updateRequestDto = BalanceUpdateRequestDto.builder()
                .id(balanceId)
                .authorisationBalance(BigDecimal.valueOf(1000))
                .factualBalance(BigDecimal.valueOf(1500))
                .build();

        account = Account.builder()
                .accountNumber("ACC123")
                .build();

        balance = Balance.builder()
                .id(balanceId)
                .account(account)
                .authorisationBalance(BigDecimal.ZERO)
                .factualBalance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void testCreateBalance_Success() {
        when(accountService.getAccountByNumber("ACC123")).thenReturn(account);
        when(balanceRepository.save(any(Balance.class))).thenReturn(balance);

        BalanceCreateResponseDto result = balanceService.createBalance(createRequestDto);

        assertNotNull(result);
        assertEquals(balanceId, result.getId());

        verify(accountService).getAccountByNumber("ACC123");
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void testUpdateBalance_Success() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        BalanceUpdateResponseDto result = balanceService.updateBalance(updateRequestDto);

        assertNotNull(result);
        assertEquals(balanceId, result.getId());

        verify(balanceRepository).findById(balanceId);
        verify(balanceRepository).save(balance);
    }

    @Test
    void testUpdateBalance_EntityNotFound() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> balanceService.updateBalance(updateRequestDto));

        verify(balanceRepository).findById(balanceId);
        verifyNoInteractions(balanceMapper);
    }

    @Test
    void testGetBalanceById_Success() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.of(balance));

        Balance result = balanceService.getBalanceById(balanceId);

        assertNotNull(result);
        assertEquals(balanceId, result.getId());
    }

    @Test
    void testGetBalanceById_NotFound() {
        when(balanceRepository.findById(balanceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> balanceService.getBalanceById(balanceId));
    }
}