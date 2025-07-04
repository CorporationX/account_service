package faang.school.accountservice.service;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BalanceServiceIntegrationTest {

    @Autowired
    private BalanceService balanceService;

    @Test
    void testEnrollNegativeAmountShouldFailValidation() {
        assertThrows(ConstraintViolationException.class, () ->
                balanceService.enroll(UUID.randomUUID(), BigDecimal.ZERO)
        );
    }

    @Test
    void testAuthorizeNegativeAmountShouldFailValidation() {
        assertThrows(ConstraintViolationException.class, () ->
                balanceService.authorize(UUID.randomUUID(), BigDecimal.ZERO)
        );
    }

    @Test
    void testCancelAuthorizationNegativeAmountShouldFailValidation() {
        assertThrows(ConstraintViolationException.class, () ->
                balanceService.cancelAuthorization(UUID.randomUUID(), BigDecimal.ZERO)
        );
    }

    @Test
    void testClearNegativeAmountShouldFailValidation() {
        assertThrows(ConstraintViolationException.class, () ->
                balanceService.clear(UUID.randomUUID(), BigDecimal.ZERO)
        );
    }
}