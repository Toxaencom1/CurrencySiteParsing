package com.taxah.currencysiteparsing.service;

import com.taxah.currencysiteparsing.model.Exchanger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {
    private final WebDriverService webDriverService;

    public List<Exchanger> parse() {
        List<Exchanger> exchangersList = new ArrayList<>();
        WebDriver driver = webDriverService.getDriver();
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
                webDriverService.closeDriver(driver);
            }
        }
    }
}
