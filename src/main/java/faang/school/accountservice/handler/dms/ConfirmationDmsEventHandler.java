package faang.school.accountservice.handler.dms;

import faang.school.accountservice.dto.dms.DmsEventDto;
import faang.school.accountservice.enums.DmsTypeOperation;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.Reserve;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
public class ConfirmationDmsEventHandler implements DmsEventHandler {
    private final ReserveService reserveService;

    @Override
    public void handle(DmsEventDto dmsEventDto) {
        reserveService.confirmReserve(dmsEventDto.getRequestId());
    }

    @Override
    public DmsTypeOperation getTypeOperation() {
        return DmsTypeOperation.CONFIRMATION;
    }
}