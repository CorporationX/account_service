package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.savings.TariffDto;
import faang.school.accountservice.dto.savings.TariffRateHistoryDto;
import faang.school.accountservice.mapper.TariffMapper;
import faang.school.accountservice.model.savings.Tariff;
import faang.school.accountservice.repository.savings.TariffRepository;
import faang.school.accountservice.service.balance.BalanceService;
import faang.school.accountservice.service.utils.UserUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

  private final TariffRepository tariffRepository;
  private final BalanceService balanceService;
  private final UserUtils userUtils;
  private final TariffMapper tariffMapper;

  @Override
  public TariffRateHistoryDto add(Long userId, TariffDto dto) {
    userUtils.validateUser(userId);
    Tariff tariff = tariffMapper.toNewEntity(dto);
    return tariffMapper.toRateHistoryDto(tariffRepository.save(tariff));
  }

  @Override
  public TariffRateHistoryDto getById(Long userId, Long id) {
    userUtils.validateUser(userId);
    return tariffMapper.toRateHistoryDto(findById(id));
  }

  @Transactional
  @Override
  public TariffRateHistoryDto update(Long userId, TariffDto dto) {
    userUtils.validateUser(userId);
    Tariff tariff = findById(dto.id());
    tariff.setTitle(dto.title());
    tariff.addNewRate(dto.rate());
    //TODO check if it works without save()
    return tariffMapper.toRateHistoryDto(tariffRepository.save(tariff));
  }

  @Override
  public Tariff findById(Long id) {
    return tariffRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("tariff not found, id = " + id));
  }
}
