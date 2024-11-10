package faang.school.accountservice.service;

import faang.school.accountservice.dto.dms.DmsEventDto;
import faang.school.accountservice.enums.DmsTypeOperation;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Reserve;
import faang.school.accountservice.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveService {
    private final ReserveRepository reserveRepository;
    private final BalanceService balanceService;
    private final AccountService accountService;

    @Value("${reserve-service.canceling-out-of-date-reserves.waiting-time}")
    private final long waitingTime;

    public Reserve getReserve(long id) {
        Optional<Reserve> reserveOpt = reserveRepository.findById(id);
        if (reserveOpt.isEmpty()) {
            String message = "Reserve with id = %d does not exist".formatted(id);
            log.error(message);
            throw new RuntimeException(message);
        }

        return reserveOpt.get();
    }

    public Reserve createReserve(Reserve reserve) {
        return reserveRepository.save(reserve);
    }

    public Reserve updateReserve(Reserve reserve) {
        return reserveRepository.save(reserve);
    }

    public Reserve getReserveByRequest(long requestId) {
         Optional<Reserve> reserveOpt = reserveRepository.findReserveByRequest(requestId);
         if (reserveOpt.isEmpty()) {
             String message = "Reserve with requestId = %d does not exist".formatted(requestId);
             log.error(message);
             throw new RuntimeException(message);
         }

         return reserveOpt.get();
    }

    @Transactional
    public void authorizeReserve(DmsEventDto dmsEventDto) {
        Account senderAccount = accountService.getCurrencyAccountByOwner(
            dmsEventDto.getSenderId(), dmsEventDto.getCurrency()
        );
        Account receiverAccount = accountService.getCurrencyAccountByOwner(
            dmsEventDto.getReceiverId(), dmsEventDto.getCurrency()
        );

        if (senderAccount.getCurrentBalance().getAuthBalance().compareTo(dmsEventDto.getAmount()) < 0) {
            String message = "Account with id = %d does not have enough funds for dms: %s"
                .formatted(senderAccount.getId(), dmsEventDto);
            log.warn(message);
            throw new RuntimeException(message);
        }

        Reserve reserve = Reserve.builder()
            .requestId(dmsEventDto.getRequestId())
            .senderAccount(senderAccount)
            .receiverAccount(receiverAccount)
            .amount(dmsEventDto.getAmount())
            .status(DmsTypeOperation.AUTHORIZATION)
            .clearScheduledAt(dmsEventDto.getClearScheduledAt())
            .build();

        Long balanceId = senderAccount.getCurrentBalance().getId();
        balanceService.decreaseAuthBalance(balanceId, dmsEventDto.getAmount());
        createReserve(reserve);
    }

    @Transactional
    public void confirmReserve(long requestId) {
        Reserve reserve = getReserveByRequest(requestId);

        Long senderBalanceId = reserve.getSenderAccount().getCurrentBalance().getId();
        balanceService.decreaseActualBalance(senderBalanceId, reserve.getAmount());

        Long receiverBalanceId = reserve.getReceiverAccount().getCurrentBalance().getId();
        balanceService.increaseAllBalances(receiverBalanceId, reserve.getAmount());

        reserve.setStatus(DmsTypeOperation.CONFIRMATION);
    }

    @Transactional
    public void cancelReserve(long requestId) {
        Reserve reserve = getReserveByRequest(requestId);
        reserve.setStatus(DmsTypeOperation.CANCELING);

        Long balanceId = reserve.getSenderAccount().getCurrentBalance().getId();
        balanceService.increaseAuthBalance(balanceId, reserve.getAmount());
    }

    @Transactional
    public void cancelOutOfDateReserves() {
        LocalDateTime dateTime = LocalDateTime.now().minusSeconds(waitingTime);
        List<Reserve> reserves = reserveRepository.findOutOfDateReserves(dateTime, DmsTypeOperation.AUTHORIZATION.name());

        reserves.forEach(
            reserve -> cancelReserve(reserve.getRequestId())
        );
    }

}
