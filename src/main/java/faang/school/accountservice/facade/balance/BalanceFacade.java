package faang.school.accountservice.facade.balance;

import faang.school.accountservice.dto.balance.BalanceRequestDto;
import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceFacade {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    public BalanceResponseDto getBalanceById(UUID balanceId) {
        Balance balance = balanceService.getBalanceById(balanceId);

        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceResponseDto(balance);
        log.info("Mapping Balance entity to BalanceResponseDto. Entity content: {}. DTO content: {}.",
                balance, balanceResponseDto);

        return balanceResponseDto;
    }

    public BalanceResponseDto authorizeBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceService.authorizeBalance(
                balanceRequestDto.getId(),
                balanceRequestDto.getAmount()
        );

        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceResponseDto(balance);
        log.info("Mapping Balance entity to BalanceResponseDto. Entity content: {}. DTO content: {}.",
                balance, balanceResponseDto);

        return balanceResponseDto;
    }

    public BalanceResponseDto clearAuthorizationBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceService.clearAuthorizationBalance(
                balanceRequestDto.getId(),
                balanceRequestDto.getAmount()
        );

        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceResponseDto(balance);
        log.info("Mapping Balance entity to BalanceResponseDto. Entity content: {}. DTO content: {}.",
                balance, balanceResponseDto);

        return balanceResponseDto;
    }

    public BalanceResponseDto cancelAuthorizationBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceService.cancelAuthorizationBalance(
                balanceRequestDto.getId(),
                balanceRequestDto.getAmount()
        );

        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceResponseDto(balance);
        log.info("Mapping Balance entity to BalanceResponseDto. Entity content: {}. DTO content: {}.",
                balance, balanceResponseDto);

        return balanceResponseDto;
    }
}
