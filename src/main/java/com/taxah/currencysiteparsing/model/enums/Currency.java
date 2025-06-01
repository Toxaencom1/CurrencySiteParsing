package com.taxah.currencysiteparsing.model.enums;

import java.util.Arrays;

public enum Currency {
    USD, EUR, RUB;

    public static Currency fromString(String currency) throws IllegalArgumentException {
        for (Currency value : Currency.values()) {
            if (value.name().equalsIgnoreCase(currency)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Неизвестная или неподдерживаемая валюта: " + currency.toUpperCase() +
                ". Поддерживаемые валюты: " +
                Arrays.toString(Currency.values()).replace("[", "").replace("]", ""));
    }
}
