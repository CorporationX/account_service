package faang.school.accountservice.mapper;

import faang.school.accountservice.util.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMapperTest {
    private final AccountMapper mapper = Mappers.getMapper(AccountMapper.class);

    @Test
    void givenAccountWhenToDtoReturnAccountDto() {
        // given - precondition
        var expectedResult = TestDataFactory.createAccount();

            // when - action
        var actualResult = mapper.toDto(expectedResult);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.id()).isEqualTo(expectedResult.getId());
        assertThat(actualResult.number()).isEqualTo(expectedResult.getNumber());
        assertThat(actualResult.type()).isEqualToIgnoringCase(expectedResult.getType().name());
        assertThat(actualResult.currency()).isEqualToIgnoringCase(expectedResult.getCurrency().name());
        assertThat(actualResult.status()).isEqualToIgnoringCase(expectedResult.getStatus().name());
        assertThat(actualResult.createdAt()).isEqualToIgnoringSeconds(expectedResult.getCreatedAt());
        assertThat(actualResult.updatedAt()).isEqualToIgnoringSeconds(expectedResult.getUpdatedAt());
        assertThat(actualResult.closedAt()).isEqualTo(expectedResult.getClosedAt());
        assertThat(actualResult.version()).isEqualTo(expectedResult.getVersion());
    }

    @Test
    void givenAccountDtoWhenToEntityReturnAccount() {
        // given - precondition
        var expectedResult = TestDataFactory.createAccountDto();

        // when - action
        var actualResult = mapper.toEntity(expectedResult);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(expectedResult.id());
        assertThat(actualResult.getNumber()).isEqualTo(expectedResult.number());
        assertThat(actualResult.getType().name()).isEqualToIgnoringCase(expectedResult.type());
        assertThat(actualResult.getCurrency().name()).isEqualToIgnoringCase(expectedResult.currency());
        assertThat(actualResult.getStatus().name()).isEqualToIgnoringCase(expectedResult.status());
        assertThat(actualResult.getCreatedAt()).isEqualToIgnoringSeconds(expectedResult.createdAt());
        assertThat(actualResult.getUpdatedAt()).isEqualToIgnoringSeconds(expectedResult.updatedAt());
        assertThat(actualResult.getClosedAt()).isEqualTo(expectedResult.closedAt());
        assertThat(actualResult.getVersion()).isEqualTo(expectedResult.version());
    }
}