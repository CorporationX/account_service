package faang.school.accountservice.service.savings;

import faang.school.accountservice.dto.savings.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savings.SavingsAccountResponseDto;
import faang.school.accountservice.model.savings.SavingsAccount;
import jakarta.validation.Valid;
import java.util.List;

public interface SavingsAccountService {

  SavingsAccountResponseDto add(Long userId, @Valid SavingsAccountCreateDto dto);

  SavingsAccount findById(Long id);

  SavingsAccountResponseDto getById(Long userId, @Valid Long id);

  List<SavingsAccountResponseDto> getSavingsDtoByOwner(Long userId, Long ownerId);
}
