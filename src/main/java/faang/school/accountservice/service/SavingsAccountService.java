package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccountResponse openSavingsAccount() {
        return null;
    }

    public SavingsAccountResponse getSavingsAccountById(Long id) {
        return null;
    }

    public SavingsAccountResponse getSavingsAccountByOwnerId(Long ownerId) {
        return null;
    }
}
