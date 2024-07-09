package faang.school.accountservice.service;

import faang.school.accountservice.dto.TariffRateChangeEvent;
import faang.school.accountservice.dto.TariffRateChangeRequestDto;
import faang.school.accountservice.mapper.TariffRateChangeRequestMapper;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.model.SavingsAccountTariffHistory;
import faang.school.accountservice.model.TariffRateChangeRequest;
import faang.school.accountservice.publisher.TariffRateChangePublisher;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.repository.TariffRateChangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TariffRateChangeRequestService {
    private final TariffRateChangeRequestRepository tariffRateChangeRequestRepository;
    private final TariffService tariffService;
    private final TariffRateChangePublisher tariffRateChangePublisher;
    private final TariffRateChangeRequestMapper mapper;
    private final SavingsAccountRepository savingsAccountRepository;

    @Transactional
    public TariffRateChangeRequestDto requestTariffRateChange(TariffRateChangeRequest request) {
        LocalDate requestedChangeDate = request.getChangeDate().toLocalDate();
        LocalDate earliestAllowedChangeDate = LocalDate.now().plusDays(1);
        if (requestedChangeDate.isBefore(earliestAllowedChangeDate)) {
            throw new IllegalArgumentException("Change date must be at least one day from now");
        }

        Optional<TariffRateChangeRequest> existingRequest = tariffRateChangeRequestRepository
                .findByTariffIdAndChangeDateAndStatus(
                request.getTariffId(), request.getChangeDate(), TariffRateChangeRequest.RequestStatus.PENDING);
        if (existingRequest.isPresent()) {
            return mapper.toDto(existingRequest.get());
        }
        
        request.setStatus(TariffRateChangeRequest.RequestStatus.PENDING);
        request.setRequestedAt(LocalDateTime.now());
        TariffRateChangeRequest savedRequest = tariffRateChangeRequestRepository.save(request);

        LocalDate notificationDate = request.getChangeDate().toLocalDate().minusDays(1);
        scheduleNotificationJob(savedRequest, notificationDate);
        if (request.getChangeDate() != null) {
            scheduleRateChangeJob(savedRequest);
        }
        return mapper.toDto(savedRequest);
    }

    private void scheduleNotificationJob(TariffRateChangeRequest request, LocalDate notificationDate) {
        Runnable notificationJob = () -> {
            List<SavingsAccount> savingsAccounts = getSavingsAccountsForTariff(request.getTariffId());
            savingsAccounts.parallelStream().forEach(savingsAccount -> {
                Long ownerId = savingsAccount.getAccount().getOwner().getId();

                TariffRateChangeEvent event = TariffRateChangeEvent.builder()
                        .savingsAccountId(savingsAccount.getId())
                        .tariffId(request.getTariffId())
                        .newRate(request.getNewRate())
                        .changeDate(request.getChangeDate())
                        .ownerId(ownerId)
                        .build();
                tariffRateChangePublisher.publish(event);
            });
            request.setStatus(TariffRateChangeRequest.RequestStatus.NOTIFIED);
            tariffRateChangeRequestRepository.save(request);
        };
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        LocalDateTime notificationDateTime = notificationDate.atStartOfDay();
        long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), notificationDateTime);
        executorService.schedule(notificationJob, delay, TimeUnit.MILLISECONDS);
    }

    private void scheduleRateChangeJob(TariffRateChangeRequest request) {
        if (request.getChangeDate() != null) {
            Runnable rateChangeJob = () -> {
                tariffService.updateTariff(request.getTariffId(), request.getNewRate());
                request.setStatus(TariffRateChangeRequest.RequestStatus.COMPLETED);
                tariffRateChangeRequestRepository.save(request);
            };
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            LocalDateTime changeDateTime = request.getChangeDate();
            long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), changeDateTime);
            executorService.schedule(rateChangeJob, delay, TimeUnit.MILLISECONDS);
        }
    }

    private List<SavingsAccount> getSavingsAccountsForTariff(Long tariffId) {
        return savingsAccountRepository.findAll().stream()
                .filter(savingsAccount -> isTariffCurrentForAccount(savingsAccount, tariffId))
                .toList();
    }

    private boolean isTariffCurrentForAccount(SavingsAccount savingsAccount, Long tariffId) {
        List<SavingsAccountTariffHistory> tariffHistory = savingsAccount.getTariffHistory();
        return tariffHistory.stream()
                .filter(history -> history.getEndDate() == null)
                .map(SavingsAccountTariffHistory::getTariff)
                .anyMatch(tariff -> tariff.getId().equals(tariffId));
    }
}