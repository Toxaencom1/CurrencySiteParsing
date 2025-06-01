package com.taxah.currencysiteparsing.service;

import com.taxah.currencysiteparsing.model.enums.Currency;
import com.taxah.currencysiteparsing.model.Exchanger;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

@Service
public class CurrencyResolver {
    private final Map<String, Function<? super Exchanger, Double>> currencySellMap = Map.of(
            "USD", Exchanger::getUsdSell,
            "EUR", Exchanger::getEurSell,
            "RUB", Exchanger::getRubSell
    );
    private final Map<String, Function<? super Exchanger, Double>> currencyBuyMap = Map.of(
            "USD", Exchanger::getUsdBuy,
            "EUR", Exchanger::getEurBuy,
            "RUB", Exchanger::getRubBuy
    );

    public Function<? super Exchanger, Double> resolveCurrency(Currency currency, boolean isTypeSell) {
        return (isTypeSell) ? currencySellMap.get(currency.name()) : currencyBuyMap.get(currency.name());
    }
}
