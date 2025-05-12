package faang.school.accountservice.constant.service;

import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.enums.Currency;
import faang.school.accountservice.entity.enums.Status;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class BalanceServiceTestConstants {
    protected static final long ACCOUNT_ID = 1;
    protected static final long BALANCE_ID = 1;
    protected static final int VERSION = 1;
    protected static final double AUTHORIZATION = 5;
    protected static final double ACTUAL_BALANCE = 10;
    protected static final Currency CURRENCY = Currency.RUB;
    protected static final Status STATUS = Status.ACTIVE;
    protected static final VerificationMode ONCE = times(1);
    protected static final VerificationMode NEVER = never();

    protected static final Account ACCOUNT = Account.builder()
            .currency(CURRENCY)
            .status(STATUS)
            .build();

    protected static final Balance BALANCE = Balance.builder()
            .id(BALANCE_ID)
            .authorizationBalance(ACTUAL_BALANCE)
            .actualBalance(ACTUAL_BALANCE)
            .account(ACCOUNT)
            .version(VERSION)
            .build();

    protected static final UpdateBalanceDto UPDATE_BALANCE_DTO = UpdateBalanceDto.builder()
            .id(BALANCE_ID)
            .authorizationBalance(AUTHORIZATION)
            .build();
}
