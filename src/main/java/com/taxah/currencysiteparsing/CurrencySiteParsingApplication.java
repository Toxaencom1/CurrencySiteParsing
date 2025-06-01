package com.taxah.currencysiteparsing;

import com.taxah.currencysiteparsing.service.facade.CurrencyFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class CurrencySiteParsingApplication implements CommandLineRunner {
    private final CurrencyFacade facade;


    public static void main(String[] args) {
        SpringApplication.run(CurrencySiteParsingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        facade.showCurrency();
    }
}
