package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffDto;

import java.util.List;

public interface TariffService {

    void addNewTariff(TariffDto tariffDto);

    void updateTariff(TariffDto tariffDto);
    // todo: с сохранением предыдущих ставок в истории

    List<TariffDto> getTariffInfo ();
}
