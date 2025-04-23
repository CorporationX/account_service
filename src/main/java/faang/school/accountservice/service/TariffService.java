package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffResponse;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;

    public TariffResponse addTariff(String typeName) {
        return null;
    }

    public void updateTariff(String typeName) {

    }

    public List<TariffResponse> getAllTariffs() {
        return null;
    }
}
