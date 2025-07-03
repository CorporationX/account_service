package faang.school.accountservice.validation.balance;

import faang.school.accountservice.exception.balance.NotEnoughAvailableFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class BalanceValidatorTest {

    @InjectMocks
    private BalanceValidator balanceValidator;
    private UUID balanceId;

    @BeforeEach
    void setUp() {
        balanceValidator = new BalanceValidator();
        balanceId = UUID.randomUUID();
    }

    @Test
    void testValidateAmountDoesNotExceedLimit_doesNotThrow() {
        BigDecimal amount = new BigDecimal("50");
        BigDecimal limit = new BigDecimal("100");

        assertThatCode(() -> balanceValidator.validateAmountDoesNotExceedLimit(balanceId, amount, limit))
                .doesNotThrowAnyException();
    }

    @Test
    void testValidateAmountDoesNotExceedLimit_throwsException() {
        BigDecimal amount = new BigDecimal("150");
        BigDecimal limit = new BigDecimal("100");

        assertThatThrownBy(() -> balanceValidator.validateAmountDoesNotExceedLimit(balanceId, amount, limit))
                .isInstanceOf(NotEnoughAvailableFundsException.class)
                .hasMessageContaining(balanceId.toString());
    }
}
