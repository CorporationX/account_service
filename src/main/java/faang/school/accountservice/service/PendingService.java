package faang.school.accountservice.service;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.dms.PendingRequestDto;
import faang.school.accountservice.dto.dms.PendingResponseDto;
import faang.school.accountservice.dto.dms.ResponseClearingDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PendingService {

    @Value("${kafka.partitions.partition-pending-response}")
    private int partitionResponse;

    @Value("${kafka.partitions.partition-clearing-response}")
    private int partitionClearingResponse;

    @Value("${kafka.topics.pending-topic}")
    private String pendingTopic;

    private final RequestService requestService;
    private final BalanceService balanceService;
    private final AccountService accountService;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void authorizationPending(PendingRequestDto requestDto) {
        Account account = accountService.getAccountByNumber(requestDto.getAccountNumber());

        if (requestDto.getRequestInputData() == null){
            requestDto.setRequestInputData(new HashMap<>());
        }
        requestDto.getRequestInputData().put("operationId", requestDto.getOperationId());

        CreateRequestDto createRequestDto = createRequestDto(requestDto, account);
        try {
            requestService.createRequest(createRequestDto);

            balanceService.authBalance(account.getBalance().getId(),
                    requestDto.getBalance().doubleValue(),
                    requestDto.getOperationId());

            kafkaProducerService.sendMessage(
                    createResponseDto(RequestStatus.PENDING, null,
                            requestDto.getOperationId()), pendingTopic, partitionResponse);
        } catch (IllegalArgumentException e) {
            kafkaProducerService.sendMessage(
                    createResponseDto(RequestStatus.FAILED, e.getMessage(),
                            requestDto.getOperationId()), pendingTopic, partitionResponse);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void clearingPending(ResponseClearingDto clearingDto) {
        try {
            Account debitAccount = accountService.getAccountByNumber(clearingDto.getAccountNumber());
            Account replenishmentAccount = accountService
                    .getAccountBuOwnerIdAndAccount(clearingDto.getRecipientId(), clearingDto.getCurrency());

            balanceService.clearingBalance(debitAccount.getBalance().getId(), clearingDto.getOperationId());
            balanceService.plusBalance(replenishmentAccount.getBalance().getId(),
                    clearingDto.getBalance().doubleValue(), clearingDto.getOperationId());

            Request request = requestService.getRequestBuValueLock(debitAccount.getOwnerId() + "");
            request.setStatus(RequestStatus.PROCESSED);

            kafkaProducerService.sendMessage(
                    createResponseDto(RequestStatus.SUCCESS, null, clearingDto.getOperationId()),
                    pendingTopic,
                    partitionClearingResponse
            );
        } catch (Exception e) {
            kafkaProducerService.sendMessage(
                    createResponseDto(RequestStatus.FAILED, e.getMessage(), clearingDto.getOperationId()),
                    pendingTopic, partitionClearingResponse);
            throw new RuntimeException(e);
        }
    }

    private CreateRequestDto createRequestDto(PendingRequestDto requestDto, Account account) {
        return CreateRequestDto.builder()
                .userId(account.getOwnerId())
                .idempotentToken(requestDto.getToken())
                .type(requestDto.getRequestType())
                .requestInputData(requestDto.getRequestInputData())
                .addictionalDetails(requestDto.getAddictionalDetail())
                .build();
    }

    private PendingResponseDto createResponseDto(RequestStatus status, String reason, String operationId) {
        return PendingResponseDto.builder()
                .requestStatus(status)
                .reason(reason)
                .operationId(operationId)
                .build();
    }
}
