package faang.school.accountservice.service;

import faang.school.accountservice.dto.payment.kafka.PaymentAuthorizationRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentAuthorizationResponseDto;
import faang.school.accountservice.dto.payment.kafka.PaymentCancelRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentCancelResponseDto;
import faang.school.accountservice.dto.payment.kafka.PaymentClearingRequestDto;
import faang.school.accountservice.dto.payment.kafka.PaymentClearingResponseDto;
import faang.school.accountservice.dto.payment.kafka.PaymentStatus;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.OperationNotAllowed;
import faang.school.accountservice.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.AUTHORIZATION_ERROR;
import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.AUTHORIZATION_SUCCESS;
import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.CANCEL_ERROR;
import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.CANCEL_SUCCESS;
import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.CLEARING_ERROR;
import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.CLEARING_SUCCESS;
import static faang.school.accountservice.dto.payment.kafka.PaymentStatus.SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class DispatcherBankOperationService {

    @Value("${spring.kafka.topic.payments.authorization.response}")
    private String topicAuthorizationResponse;
    @Value("${spring.kafka.topic.payments.clearing.response}")
    private String topicClearingResponse;
    @Value("${spring.kafka.topic.payments.cancel.response}")
    private String topicCancelResponse;

    private final KafkaProducer kafkaProducer;
    private final BalanceService balanceService;

    public PaymentAuthorizationResponseDto authorizationOperation(PaymentAuthorizationRequestDto paymentAuthorizationRequestDto) {
        UUID operationId = paymentAuthorizationRequestDto.operationId();
        UUID accountId = paymentAuthorizationRequestDto.accountId();
        BigDecimal amount = paymentAuthorizationRequestDto.amount();

        String description;
        PaymentStatus paymentStatus;

        try {
            balanceService.authorize(accountId, amount);
            paymentStatus = AUTHORIZATION_SUCCESS;
            description = "Successful authorization";
        } catch (OperationNotAllowed | BalanceNotFoundException e) {
            paymentStatus = AUTHORIZATION_ERROR;
            description = e.getMessage();
        } catch (Exception e) {
            paymentStatus = SERVER_ERROR;
            description = e.getMessage();
        }
        PaymentAuthorizationResponseDto paymentAuthorizationResponseDto = new PaymentAuthorizationResponseDto(operationId,
                paymentStatus, description);

        kafkaProducer.sendMessage(topicAuthorizationResponse, paymentAuthorizationResponseDto);

        return paymentAuthorizationResponseDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public PaymentClearingResponseDto clearingOperation(PaymentClearingRequestDto paymentClearingRequestDto) {
        UUID operationId = paymentClearingRequestDto.operationId();
        UUID senderAccountId = paymentClearingRequestDto.senderAccountId();
        UUID recipientAccountId = paymentClearingRequestDto.recipientAccountId();
        BigDecimal amount = paymentClearingRequestDto.amount();

        String description;
        PaymentStatus paymentStatus;
        try {
            balanceService.clearing(senderAccountId, amount);
            balanceService.admission(recipientAccountId, amount);
            paymentStatus = CLEARING_SUCCESS;
            description = "Successful clearing";
        } catch (OperationNotAllowed | BalanceNotFoundException e) {
            paymentStatus = CLEARING_ERROR;
            description = e.getMessage();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (Exception e) {
            paymentStatus = SERVER_ERROR;
            description = e.getMessage();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        PaymentClearingResponseDto paymentClearingResponseDto = new PaymentClearingResponseDto(operationId,
                paymentStatus, description);

        kafkaProducer.sendMessage(topicClearingResponse, paymentClearingResponseDto);

        return paymentClearingResponseDto;
    }

    public PaymentCancelResponseDto cancelOperation(PaymentCancelRequestDto paymentCancelRequestDto) {
        UUID operationId = paymentCancelRequestDto.operationId();
        UUID accountId = paymentCancelRequestDto.accountId();
        BigDecimal amount = paymentCancelRequestDto.amount();

        String description;
        PaymentStatus paymentStatus;

        try {
            balanceService.cancelAuthorization(accountId, amount);
            paymentStatus = CANCEL_SUCCESS;
            description = "Successful cancel";
        } catch (OperationNotAllowed | BalanceNotFoundException e) {
            paymentStatus = CANCEL_ERROR;
            description = e.getMessage();
        } catch (Exception e) {
            paymentStatus = SERVER_ERROR;
            description = e.getMessage();
        }
        PaymentCancelResponseDto paymentCancelResponseDto = new PaymentCancelResponseDto(operationId,
                paymentStatus, description);

        kafkaProducer.sendMessage(topicCancelResponse, paymentCancelResponseDto);

        return paymentCancelResponseDto;
    }

}
