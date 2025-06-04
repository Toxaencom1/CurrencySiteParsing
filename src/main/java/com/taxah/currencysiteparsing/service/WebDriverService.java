package com.taxah.currencysiteparsing.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebDriverService {
    private final List<WebDriver> activeDrivers = new CopyOnWriteArrayList<>();
    private volatile boolean isShuttingDown = false;
    private final ObjectProvider<WebDriver> webDriverProvider;

    @PostConstruct
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал завершения работы");
            isShuttingDown = true;
            cleanUp();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Завершение работы выполнено");
        }));
        log.info("Зарегистрирован hook завершения работы");
    }

    private void cleanUp() {
        if (activeDrivers.isEmpty()) {
            log.info("Нет активных драйверов для закрытия");
            return;
        }
        log.info("Начало процесса закрытия драйверов. Активных драйверов: {}", activeDrivers.size());
        activeDrivers.forEach(driver -> {
            try {
                driver.quit();
                System.out.println("Драйвер успешно закрыт.");
            } catch (Exception e) {
                log.error("Ошибка при закрытии драйвера: {}", e.getMessage());
            }
        });
        activeDrivers.clear();
        System.out.println("Все драйверы закрыты");
    }

    public WebDriver getDriver() {
        if (isShuttingDown) {
            throw new IllegalStateException("Приложение находится в процессе завершения работы");
        }
        WebDriver driver = webDriverProvider.getObject();
        activeDrivers.add(driver);
        return driver;
    }

    public void closeDriver(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                log.info("Драйвер успешно закрыт");
            } catch (Exception e) {
                log.error("Ошибка при закрытии драйвера: {}", e.getMessage());
            } finally {
                boolean removed = activeDrivers.remove(driver);
                if (!removed) {
                    log.warn("Драйвер не был найден в списке активных драйверов");
                }
            }
        }
    }
}
