package faang.school.accountservice.mapper;

import faang.school.accountservice.enums.account.AccountType;
import faang.school.accountservice.model.FreeAccountNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreeAccountNumberMapperTest {
    private FreeAccountNumberMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(FreeAccountNumberMapper.class);
    }

    @Test
    void testToFreeAccountNumber() {
        String expected = "5536000000000001";
        AccountType accountType = AccountType.SAVINGS;
        Long currentNumber = 1L;
        int numberLength = 12;

        FreeAccountNumber result = mapper.toFreeAccountNumber(accountType, currentNumber, numberLength);

        assertEquals(expected, result.getAccountNumber());
    }
}