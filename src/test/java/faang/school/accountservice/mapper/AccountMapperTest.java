package faang.school.accountservice.mapper;

import faang.school.accountservice.data.AccountTestData;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AccountMapperTest {
    private final AccountMapper mapper = new AccountMapperImpl();
    private AccountDto dto;
    private Account entity;


    @BeforeEach
    void setUp() {
        var testData = new AccountTestData();
        dto = testData.getAccountDto();
        entity = testData.getAccountEntity();
    }

    @Test
    void toDtoTest() {
        AccountDto actualResult = mapper.toDto(entity);

        assertEquals(dto, actualResult);
    }

    @Test
    void toEntityTest() {
        entity.setVersion(null);

        Account actualResult = mapper.toEntity(dto);

        assertEquals(entity, actualResult);
    }
}