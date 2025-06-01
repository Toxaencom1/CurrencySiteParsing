package com.taxah.currencysiteparsing.config;

import com.taxah.currencysiteparsing.model.AuthData;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.text.DecimalFormat;

@Configuration
public class AppConfig {

    @Bean
    public AuthData authData(@Value("${email.from.user}") String fromUser,
                             @Value("${email.from.pass}") String mailPassword,
                             @Value("${email.to}") String emailRecipients,
                             @Value("${email.topic}") String topicName) {
        return AuthData.builder()
                .usernameFrom(fromUser)
                .mailPassword(mailPassword)
                .emailRecipients(emailRecipients)
                .topicName(topicName)
                .build();
    }

    @Bean
    @Scope("prototype") // новый при каждом вызове
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }

    @Bean
    public DecimalFormat decimalFormat() {
        return new DecimalFormat("#.###");
    }
}
