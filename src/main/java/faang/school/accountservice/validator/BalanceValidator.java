package faang.school.accountservice.validator;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.DataBalanceValidation;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceValidator {

    private final BalanceRepository balanceRepository;

    public void checkIsNull(BalanceDto balanceDto) {
        if (balanceDto == null) {
            log.error("The argument balanceDto cannot be null");
            throw new DataBalanceValidation("The argument balanceDto cannot be null");
        }
    }

    public void checkIsNull(Long balanceId) {
        if (balanceId == null) {
            log.error("The argument balanceId cannot be null");
            throw new DataBalanceValidation("The argument balanceId cannot be null");
        }
    }

    public void checkExistsBalanceInBd(BalanceDto balanceDto) {
        if (!balanceRepository.existsById(balanceDto.getId())) {
            throw new DataBalanceValidation("The balance with id " + balanceDto.getId() + " does not exist in the database");
        }
    }
}