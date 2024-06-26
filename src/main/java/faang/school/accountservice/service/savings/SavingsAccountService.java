package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.dto.savings.SavingsAccountToCreateDto;

public interface SavingsAccountService {

    SavingsAccountDto openSavingsAccount(SavingsAccountToCreateDto dto);

    SavingsAccountDto getSavingsAccount(Long id);
}
