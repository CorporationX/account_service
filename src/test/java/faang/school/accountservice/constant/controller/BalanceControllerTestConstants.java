package faang.school.accountservice.constant.controller;

import faang.school.accountservice.entity.enums.Currency;
import faang.school.accountservice.entity.enums.Status;
import faang.school.accountservice.util.BaseContextTest;

import java.util.Random;

public class BalanceControllerTestConstants extends BaseContextTest {
    protected static final Random RANDOM = new Random();
    protected static final long INVALID_ID = 0;
    protected static final double INVALID_BALANCE = -1;
    protected static final long NOT_EXISTS_ID = Long.MAX_VALUE;
    protected static final long USER_ID = 1;

    protected static final String ROOT_URL = "/api/v1/balances";
    protected static final String GET_BALANCE_URL = ROOT_URL + "/{balanceId}";
    protected static final String UPDATE_URL = ROOT_URL;

    protected static final String JSON_EMPTY = "{}";
    protected static final String X_USER_ID = "x-user-id";

    protected static final Currency CURRENCY = Currency.RUB;
    protected static final Status STATUS = Status.ACTIVE;
    protected static final double ACTUAL_BALANCE = 10;
    protected static final double AUTHORIZATION_BALANCE = 10;
    protected static final double AUTHORIZATION = 5;

    protected static final String INVALID_BALANCE_ID_MSG = "should be more than 1";
    protected static final String INVALID_BALANCE_MSG = "should be more than 0";
    protected static final String BALANCE_NOT_FOUND_MSG = "Balance with id " + NOT_EXISTS_ID + " not exists";
    protected static final String FIND_INVALID_BALANCE_ID_MSG = "find.balanceId: " + INVALID_BALANCE_ID_MSG;
    protected static final String UPDATE_INVALID_BALANCE_ID_MSG = "{\"id\":\"" + INVALID_BALANCE_ID_MSG + "\"}";
    protected static final String INVALID_ACTUAL_BALANCE_MSG = "{\"actualBalance\":\"" + INVALID_BALANCE_MSG + "\"}";
    protected static final String INVALID_AUTHORIZATION_BALANCE_MSG =
            "{\"authorizationBalance\":\"" + INVALID_BALANCE_MSG + "\"}";
}
