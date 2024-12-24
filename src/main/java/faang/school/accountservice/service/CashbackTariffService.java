package faang.school.accountservice.service;

import faang.school.accountservice.dto.cashbackdto.CashbackTariffDto;
import faang.school.accountservice.model.CashbackTariff;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import faang.school.accountservice.repository.CashbackTariffRepository;
import faang.school.accountservice.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CashbackTariffService {

    private final CashbackTariffRepository tariffRepository;
    private final CashbackOperationMappingRepository operationMappingRepository;
    private final CashbackMerchantMappingRepository merchantMappingRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public Long createTariff() {
        CashbackTariff tariff = new CashbackTariff();
        tariff.setCreatedAt(LocalDateTime.now());
        return tariffRepository.save(tariff).getId();
    }

    public CashbackTariffDto getTariffById(Long id) {
        CashbackTariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tariff not found with id " + id));
        return new CashbackTariffDto(
                tariff.getId(),
                tariff.getCreatedAt(),
                operationMappingRepository.findByTariffId(id),
                merchantMappingRepository.findByTariffId(id)
        );
    }

}