package faang.school.accountservice.service;

import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffAssignmentService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final TariffRepository tariffRepository;

    public void assignTariffOnSavingsAccount(Long accountId, Long tariffId) {

    }
}
