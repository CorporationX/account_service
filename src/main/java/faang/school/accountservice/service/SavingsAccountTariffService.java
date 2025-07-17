package faang.school.accountservice.service;

import faang.school.accountservice.entity.Tariff;
import faang.school.accountservice.entity.TariffRateHistory;
import faang.school.accountservice.repository.TariffRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsAccountTariffService {

    private final TariffRepository savingsAccountTariffRepository;

    public Tariff addTariffForAccount(String type, Double rate) {
        return Tariff.builder()
                .type(type)
                .historyRates()
    }

    public Tariff updateTariffForAccount(Long id, Double rate) {
        Tariff tariff = getTariffAccountById(id);
        List<TariffRateHistory> history = tariff.getHistoryRates();

        //как сохранять историю при обновлении тарифа??

    }

    @Transactional(readOnly = true)
    public List<Tariff> getAllTariffs() {
        return savingsAccountTariffRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tariff getTariffAccountById(Long id) {
        return savingsAccountTariffRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Tariff with id:d% not found".formatted(id))
        );
    }

    /*
    1. добавление нового тарифа
    2. изменение ставки у существующего тарифа
    3. получение информации о тарифах
     */


}
