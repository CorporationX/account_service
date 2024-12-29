package faang.school.accountservice.enums;

import lombok.Getter;

@Getter
public enum Currency {
    RUB(810, "₽"),
    EUR(978, "€"),
    USD(840, "$");

    private final int currencyNumber;
    private final String currencySymbol;

    Currency(int currencyNumber, String currencySymbol) {
        this.currencyNumber = currencyNumber;
        this.currencySymbol = currencySymbol;
    }
}