package com.taxah.currencysiteparsing.service;

import com.taxah.currencysiteparsing.model.enums.ActionType;
import com.taxah.currencysiteparsing.model.enums.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CurrencyIOService {
    private final String path = "lastCurrency.txt";

    public double currencyRead(Currency currency, ActionType type) {
        try {
            Path filePath = Paths.get(path);
            if (Files.exists(filePath)) {
                List<String> fileLines = Files.readAllLines(filePath);
                for (String line : fileLines) {
                    if (line.startsWith(type.name() + ":" + currency.name())) {
                        String currencyValue = line.split(":")[2].trim();
                        log.info("Прочитано из файла: {}", currencyValue);
                        return Double.parseDouble(currencyValue);
                    }
                }
                throw new IOException("Файл не содержит данного курса");
            }
        } catch (IOException | NumberFormatException e) {
            log.warn(e.getMessage());
        }
        return Double.MAX_VALUE;
    }

    public void currencyWrite(Currency currency, double value, ActionType type) {
        try {
            Path filePath = Paths.get(path);
            List<String> updatedLines = new ArrayList<>();
            String prefix = type.name() + ":" + currency.name() + ":";

            if (Files.exists(filePath)) {
                List<String> fileLines = Files.readAllLines(filePath);
                boolean updated = false;

                for (String line : fileLines) {
                    if (line.startsWith(prefix)) {
                        updatedLines.add(prefix + value);
                        updated = true;
                    } else {
                        updatedLines.add(line);
                    }
                }
                if (!updated) {
                    updatedLines.add(prefix + value);
                }
            } else {
                updatedLines.add(prefix + value);
            }

            Files.write(filePath, updatedLines);
            log.info("Записано в файл: {}:{}:{}",type, currency.name(), value);

        } catch (IOException e) {
            log.error("Ошибка записи: {}", e.getMessage());
        }
    }
}
