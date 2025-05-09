package faang.school.accountservice.service.savingsaccount;

import faang.school.accountservice.dto.savingsaccount.DepositDto;
import faang.school.accountservice.dto.savingsaccount.OpenSavingsAccountDto;
import faang.school.accountservice.dto.savingsaccount.SavingsAccountResponseDto;
import faang.school.accountservice.dto.savingsaccount.WithdrawDto;

public interface SavingsAccountService {
    SavingsAccountResponseDto openSavingsAccount(OpenSavingsAccountDto accountDto);
    SavingsAccountResponseDto getSavingsAccountById(String accountId);
    SavingsAccountResponseDto changeTariff(Long savingsAccountId, Long newTariffId);
    SavingsAccountResponseDto deposit(DepositDto depositDto);
    SavingsAccountResponseDto withdraw(WithdrawDto withdrawDto);
}
