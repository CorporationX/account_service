package faang.school.accountservice.validator;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.DataBalanceValidation;
import faang.school.accountservice.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceValidatorTest {

    @InjectMocks
    private BalanceValidator balanceValidator;

    @Mock
    private BalanceRepository balanceRepository;

    private BalanceDto balanceDto;
    private BalanceDto balanceDtoNull;
    private Long balanceId;
    private Long balanceIdNull;

    @BeforeEach
    public void setUp() {
        balanceDto = BalanceDto.builder().id(1L).accountId(5L).build();
        balanceId = 1L;
    }

    @Test
    public void testCheckIsNullBalanceDto() {
        assertThrows(DataBalanceValidation.class, () -> balanceValidator.checkIsNull(balanceDtoNull));
    }

    @Test
    public void testCheckIsNullBalanceId() {
        assertThrows(DataBalanceValidation.class, () -> balanceValidator.checkIsNull(balanceIdNull));
    }

    @Test
    public void testCheckExistsBalanceByIdInBd() {
        when(balanceRepository.existsById(balanceId)).thenReturn(false);
        assertThrows(DataBalanceValidation.class, () -> balanceValidator.checkExistsBalanceByIdInBd(balanceDto));
    }

    @Test
    public void testCheckAbsenceBalanceByAccountIdInBd() {
        when(balanceRepository.existsByAccountId(balanceDto.getAccountId())).thenReturn(true);
        assertThrows(DataBalanceValidation.class, () -> balanceValidator.checkAbsenceBalanceByAccountIdInBd(balanceDto));
    }
}
