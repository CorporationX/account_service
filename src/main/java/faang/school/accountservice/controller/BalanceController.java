package faang.school.accountservice.controller;

import faang.school.Check;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.event.request.AuthorizationRequestEvent;
import faang.school.accountservice.dto.event.request.CancellationRequestEvent;
import faang.school.accountservice.dto.event.request.ClearRequestEvent;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.publisher.AuthorizationRequestPublisher;
import faang.school.accountservice.publisher.CancellRequestPublisher;
import faang.school.accountservice.publisher.ClearRequestPublisher;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.validation.ValidAccountNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
@Validated
@Log4j2
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;
    private final AuthorizationRequestPublisher authorizationRequestPublisher;
    private final ClearRequestPublisher clearRequestPublisher;
    private final CancellRequestPublisher cancellRequestPublisher;

    @GetMapping("/accounts/uuids/{accountUUID}")
    public ResponseEntity<BalanceDto> getBalanceByUUID(@PathVariable UUID accountUUID) {
        Balance balance = balanceService.getBalanceByAccountId(accountUUID);
        BalanceDto balanceDto = balanceMapper.toDto(balance);
        return ResponseEntity.ok(balanceDto);
    }

    @GetMapping("/accounts/numbers/{accountNumber}")
    public ResponseEntity<BalanceDto> getBalanceByAccountNumber(@PathVariable @ValidAccountNumber String accountNumber) {
        Balance balance = balanceService.getBalanceByAccountNumber(accountNumber);
        BalanceDto balanceDto = balanceMapper.toDto(balance);
        return ResponseEntity.ok(balanceDto);
    }

    @PostMapping("/authorize")
    public ResponseEntity<AuthorizationRequestEvent> authorize(@RequestBody AuthorizationRequestEvent authorizationRequestEvent) {

        AuthorizationRequestEvent transfer = AuthorizationRequestEvent.builder()
                .authorizationId(UUID.randomUUID())
                .userId(authorizationRequestEvent.getUserId())
                .sourceId(authorizationRequestEvent.getSourceId())
                .targetId(authorizationRequestEvent.getTargetId())
                .amount(authorizationRequestEvent.getAmount())
                .currency(authorizationRequestEvent.getCurrency())
                .build();

        authorizationRequestPublisher.publish(transfer);

        return ResponseEntity.ok(transfer);
    }

    @PostMapping("/clear")
    public ResponseEntity<Void> clear(@RequestBody ClearRequestEvent clearRequestEvent) {
        clearRequestPublisher.publish(clearRequestEvent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancel(@RequestBody CancellationRequestEvent cancellationRequestEvent) {
        cancellRequestPublisher.publish(cancellationRequestEvent);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/enrolls")
    public ResponseEntity<BalanceDto> enrollBalance(@RequestBody Check.UpdateBalanceDto updateBalanceDto) {
        Balance balance = balanceService.deposit(updateBalanceDto.accountId(), updateBalanceDto.balanceDelta());
        BalanceDto balanceDto = balanceMapper.toDto(balance);
        return ResponseEntity.ok(balanceDto);
    }
}