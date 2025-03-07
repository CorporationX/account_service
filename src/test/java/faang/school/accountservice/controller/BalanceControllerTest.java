package faang.school.accountservice.controller;

import faang.school.accountservice.service.balance.BalanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BalanceControllerTest {
    @Mock
    private BalanceService balanceServiceMock;
    @InjectMocks
    private BalanceController balanceController;
    @Test
    @DisplayName("Test getting balance")
    void testGetBalance() {
        Long balanceId = 1L;
        balanceController.getBalance(balanceId);
        Mockito.verify(balanceServiceMock, Mockito.times(1)).getBalance(balanceId);
    }
}