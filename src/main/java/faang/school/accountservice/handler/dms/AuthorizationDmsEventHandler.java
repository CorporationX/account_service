package faang.school.accountservice.handler.dms;

import faang.school.accountservice.dto.dms.DmsEventDto;
import faang.school.accountservice.enums.DmsTypeOperation;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Reserve;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationDmsEventHandler implements DmsEventHandler {
    private final ReserveService reserveService;

    @Override
    public void handle(DmsEventDto dmsEventDto) {
        reserveService.authorizeReserve(dmsEventDto);
    }

    @Override
    public DmsTypeOperation getTypeOperation() {
        return DmsTypeOperation.AUTHORIZATION;
    }
}
