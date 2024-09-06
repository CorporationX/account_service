package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreeAccountNumberService {
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public String getNextSavingsAccountNumber(){
        var savingsAccountNumber = freeAccountNumbersRepository.getSavingsAccountNumber(AccountType.SAVINGSACCOUNT);
        freeAccountNumbersRepository.deleteSavingsAccountNumber(savingsAccountNumber);
        return savingsAccountNumber;
    }

}
