package com.taxah.currencysiteparsing.model.enums;

import java.util.Arrays;

public enum ActionType {
    BUY, SELL;

    public static ActionType fromString(String type) throws IllegalArgumentException {
        for (ActionType value : ActionType.values()) {
            if (value.name().equalsIgnoreCase(type)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Неизвестная или неподдерживаемая действие: " + type.toUpperCase() +
                ". Поддерживаемые действия: " +
                Arrays.toString(ActionType.values()).replace("[", "").replace("]", ""));
    }
}
