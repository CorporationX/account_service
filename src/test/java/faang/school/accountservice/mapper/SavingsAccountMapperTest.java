package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountMapperTest {

    private final SavingsAccountMapper mapper = Mappers.getMapper(SavingsAccountMapper.class);

    @Test
    void toSavingsAccountResponseDto_ShouldMapAllFieldsCorrectly() {
        Account account = new Account();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime interestDate = now.minusDays(1);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setAccount(account);
        savingsAccount.setBalance(BigDecimal.valueOf(1000.50));
        savingsAccount.setTariffHistory(List.of(1L, 2L, 3L));
        savingsAccount.setLastInterestDate(interestDate);
        savingsAccount.setVersion(1L);
        savingsAccount.setCreatedAt(now.minusDays(5));
        savingsAccount.setUpdatedAt(now);

        SavingsAccountResponseDto dto = mapper.toSavingsAccountResponseDto(savingsAccount);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(BigDecimal.valueOf(1000.50), dto.getBalance());
        assertEquals(Arrays.asList(1L, 2L, 3L), dto.getTariffHistory());
        assertEquals(interestDate, dto.getLastInterestDate());
        assertEquals(1L, dto.getVersion());
        assertEquals(now.minusDays(5), dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void toSavingsAccountResponseDto_ShouldHandleNullValues() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setBalance(BigDecimal.ZERO);
        savingsAccount.setTariffHistory(new ArrayList<>());

        SavingsAccountResponseDto dto = mapper.toSavingsAccountResponseDto(savingsAccount);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNotNull(dto.getAccountId());
        assertEquals(BigDecimal.ZERO, dto.getBalance());
        assertNotNull(dto.getTariffHistory());
        assertTrue(dto.getTariffHistory().isEmpty());
        assertNull(dto.getLastInterestDate());
        assertNull(dto.getVersion());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void toSavingsAccountResponseDto_ShouldMapEmptyTariffHistory() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setBalance(BigDecimal.ZERO);
        savingsAccount.setTariffHistory(new ArrayList<>());

        SavingsAccountResponseDto dto = mapper.toSavingsAccountResponseDto(savingsAccount);

        assertNotNull(dto);
        assertNotNull(dto.getTariffHistory());
        assertTrue(dto.getTariffHistory().isEmpty());
    }

    @Test
    void toSavingsAccountResponseDto_ShouldMapSingleTariffInHistory() {
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setBalance(BigDecimal.ZERO);
        savingsAccount.setTariffHistory(List.of(5L));

        SavingsAccountResponseDto dto = mapper.toSavingsAccountResponseDto(savingsAccount);

        assertNotNull(dto);
        assertEquals(1, dto.getTariffHistory().size());
        assertEquals(5L, dto.getTariffHistory().get(0));
    }

    @Test
    void toSavingsAccountResponseDto_ShouldMapDateFormatCorrectly() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 15, 14, 30);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(1L);
        savingsAccount.setBalance(BigDecimal.ZERO);
        savingsAccount.setTariffHistory(List.of(1L));
        savingsAccount.setCreatedAt(dateTime);
        savingsAccount.setUpdatedAt(dateTime);

        SavingsAccountResponseDto dto = mapper.toSavingsAccountResponseDto(savingsAccount);

        assertNotNull(dto);
        assertEquals(dateTime, dto.getCreatedAt());
        assertEquals(dateTime, dto.getUpdatedAt());
    }
}