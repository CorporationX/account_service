package faang.school.accountservice.service;

import faang.school.accountservice.enums.AccountType;

import java.math.BigInteger;

public interface FreeAccountNumberService {

    BigInteger getFreeAccountNumber(AccountType accountType);

}
