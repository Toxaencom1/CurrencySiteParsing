package com.taxah.currencysiteparsing.service.facade;

import com.taxah.currencysiteparsing.model.Exchanger;
import com.taxah.currencysiteparsing.model.enums.ActionType;
import com.taxah.currencysiteparsing.model.enums.Currency;
import com.taxah.currencysiteparsing.service.*;
import com.taxah.currencysiteparsing.service.validator.AuthDataValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyFacade {
    private final ParserService parserService;
    private final CurrencyIOService storage;
    private final StreamService streamService;
    private final EmailNotificationService emailNotification;
    private final DecimalFormat decimalFormater;
    private final CurrencyResolver currencyResolver;
    private final AuthDataValidator authDataValidator;

    @Value("${parsing.delay}")
    private int delay;
    @Value("${always}")
    private boolean alwaysNotify;
    @Value("${sum}")
    private double moneyAmount;
    @Value("${currency}")
    private String stringCurrency;
    @Value("${type}")
    private String stringType;

    public void showCurrency() {
        showDescription();
        if (!authDataValidator.validateData()) {
            return;
        }
        try {
            ActionType type = ActionType.fromString(stringType);
            boolean isTypeSell = (type == ActionType.SELL);
            Currency currency = Currency.fromString(stringCurrency);
            Function<? super Exchanger, Double> mappingFunction = currencyResolver.resolveCurrency(currency, isTypeSell);
            double lastStorageValue = storage.currencyRead(currency, type);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            while (true) {
                List<Exchanger> parsedExchangers = parserService.parse();
                double nowValue = streamService.valueResolver(parsedExchangers, mappingFunction, isTypeSell);
                List<Exchanger> filteredExchangers =
                        streamService.filterByFunction(parsedExchangers, nowValue, mappingFunction);

                double resolvedValue = (isTypeSell) ? (this.moneyAmount / nowValue) : (this.moneyAmount * nowValue);
                String sumString = "Расчет валюты: " + decimalFormater.format(resolvedValue) + "\n";

                boolean profitable = (isTypeSell) ? (nowValue < lastStorageValue) : (nowValue > lastStorageValue);
                if (alwaysNotify || (lastStorageValue != Double.MAX_VALUE && profitable)) {
                    emailNotification.sendCurrenciesMessage("Дата: " + LocalDateTime.now().format(timeFormatter) +
                            "\n" + filteredExchangers + sumString);
                }
                if (nowValue != lastStorageValue) {
                    storage.currencyWrite(currency, nowValue, type);
                    lastStorageValue = nowValue;
                }
                System.out.println("\n⏱️-=Временная метка: " + LocalDateTime.now().format(timeFormatter) + "=-");
                filteredExchangers.forEach(System.out::println);
                System.out.println(((isTypeSell) ? "Покупка валюты - " : "Продажа валюты - ") + currency.name() + "\n");
                System.out.println(((isTypeSell) ? "Минимальный курс: " : "Максимальный курс: ") +
                        decimalFormater.format(nowValue) + "\n");
                System.out.println("Количество доступных обменников: " + filteredExchangers.size() + "\n");
                if (this.moneyAmount != 0.0) {
                    System.out.println(sumString);
                }
                Thread.sleep(1000L * 60 * delay);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void showDescription() {
        log.info("Ссылка как сделать `пароль приложения` для Google Mail аккаунта \"https://support.google.com/accounts/answer/185833?hl=ru\"");
        log.info("Флаги для использования: --email.from.user=\"?\" --email.from.pass=\"?\"");
        log.info("--email.to=\"?\" - Указывает на какой/какие адреса отправлять письмо, адреса можно указывать через запятую. Указывать несколько нужно в кавычках.");
        log.info("--type=? - Тип операции, доступные варианты: {}. Описание: SELL для просмотра к покупке валюты, BUY для просмотра к продаже валюты.",
                Arrays.toString(ActionType.values()).replace("[", "").replace("]", ""));
        log.info("--currency=? - Валюта для расчета по курсу, доступные варианты: {}. По умолчанию USD",
                Arrays.toString(Currency.values()).replace("[", "").replace("]", ""));
        log.info("--parsing.delay=? - Задержка между парсингами в минутах, по умолчанию 15 минут.");
        log.info("--sum=? - Количество вашей валюты для расчета по курсу.");
        log.info("--always=? - Изменяет поведение отправки письма, если true отправляет даже при отсутствии изменений в курсе, по умолчанию: false");
        log.info("Пример: --email.from.user=example@gmail.com --email.from.pass=\"**** **** **** ****\" --parsing.delay=10 --sum=50000 --currency=USD --type=sell");
    }
}
