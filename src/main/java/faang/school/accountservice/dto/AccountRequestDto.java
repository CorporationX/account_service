package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

public class AccountRequestDto {

    /** Тип владельца счета */
    private OwnerType ownerType;

    /** Тип счета */
    private AccountType accountType;

    /** Код валюты */
    private Currency currency;
}
