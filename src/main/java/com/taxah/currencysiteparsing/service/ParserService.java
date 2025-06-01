package com.taxah.currencysiteparsing.service;

import com.taxah.currencysiteparsing.model.Exchanger;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {

    private final List<WebDriver> activeDrivers = new CopyOnWriteArrayList<>();
    private volatile boolean isShuttingDown = false;
    private final ObjectProvider<WebDriver> webDriverProvider;

    public List<Exchanger> parse() {
        List<Exchanger> exchangersList = new ArrayList<>();
        WebDriver driver = getDriver();
        try {
            driver.get("https://kurs.kz/site/index?city=almaty");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table-kurs")));
            WebElement table = driver.findElement(By.cssSelector("table.table-kurs"));

            List<WebElement> rows = table.findElements(By.cssSelector("tbody > tr"));
            for (WebElement row : rows) {
                try {
                    Exchanger exchanger = Exchanger.builder().build();
                    List<WebElement> tooltip = row.findElements(By.cssSelector("div.tooltip-container"));
                    if (tooltip.isEmpty()) {
                        continue;
                    } else {
                        WebElement relativeTime = row.findElement(By.cssSelector("span.relativeTime"));
                        String text = relativeTime.getAttribute("textContent");
                        if (text != null &&
                                (text.contains("час назад") || text.contains("часа назад") || text.contains("часов назад"))) {
                            continue;
                        }
                    }

                    exchanger.setExchangerInfo(tooltip.get(0).findElement(By.cssSelector("a.tab")).getText());
                    exchanger.setAddress(tooltip.get(0).findElement(By.cssSelector("span.address address")).getText());

                    List<WebElement> spans = row.findElements(By.cssSelector("span[title]"));
                    for (WebElement span : spans) {
                        String title = span.getAttribute("title");
                        String value = span.getAttribute("textContent");
                        if (title != null) {
                            if (title.contains("USD - покупка")) {
                                exchanger.setUsdBuy(Double.parseDouble(Objects.requireNonNull(value)));
                            } else if (title.contains("USD - продажа")) {
                                exchanger.setUsdSell(Double.parseDouble(Objects.requireNonNull(value)));
                            } else if (title.contains("EUR - покупка")) {
                                exchanger.setEurBuy(Double.parseDouble(Objects.requireNonNull(value)));
                            } else if (title.contains("EUR - продажа")) {
                                exchanger.setEurSell(Double.parseDouble(Objects.requireNonNull(value)));
                            } else if (title.contains("RUB - покупка")) {
                                exchanger.setRubBuy(Double.parseDouble(Objects.requireNonNull(value)));
                            } else if (title.contains("RUB - продажа")) {
                                exchanger.setRubSell(Double.parseDouble(Objects.requireNonNull(value)));
                            }
                        }
                    }
                    exchangersList.add(exchanger);
                } catch (Exception e) {
                    log.error("Ошибка парсинга строки: {}", e.getMessage());
                }
            }
            return exchangersList;
        } catch (IllegalStateException e) {
            log.warn("Невозможно создать драйвер: приложение завершает работу");
            return exchangersList;
        } finally {
            if (driver != null) {
                closeDriver(driver);
            }
        }
    }

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

    protected WebDriver getDriver() {
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
