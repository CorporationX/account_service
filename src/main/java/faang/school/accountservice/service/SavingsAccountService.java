package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.repository.SavingsAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsAccountMapper savingsAccountMapper;

    @Transactional
    public SavingsAccountDto openSavingsAccount(SavingsAccountDto savingsAccountDto) {
        SavingsAccount savingsAccount = savingsAccountMapper.toEntity(savingsAccountDto);
        savingsAccount.setVersion(1);
        savingsAccount.setCreatedAt(LocalDateTime.now());
        savingsAccount.setUpdatedAt(LocalDateTime.now());
        savingsAccount.setTariffHistory("1");
        savingsAccountRepository.save(savingsAccount);
        savingsAccountDto.setId(savingsAccount.getId());
        return savingsAccountDto;
    }
}
