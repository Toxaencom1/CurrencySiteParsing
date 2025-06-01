package com.taxah.currencysiteparsing.service;

import com.taxah.currencysiteparsing.model.AuthData;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Properties;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationService {
    private final AuthData authData;

    public void sendCurrenciesMessage(String messageText) {
        if (authData.getEmailRecipients().equals("example@email.com")) {
            authData.setEmailRecipients(authData.getUsernameFrom());
        }
        String[] toEmails = authData.getEmailRecipients().split(",");
        log.info(Arrays.toString(toEmails));

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(authData.getUsernameFrom(), authData.getMailPassword());
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(authData.getUsernameFrom()));
            InternetAddress[] addresses = new InternetAddress[toEmails.length];
            for (int i = 0; i < toEmails.length; i++) {
                addresses[i] = new InternetAddress(toEmails[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(authData.getTopicName());
            message.setText(messageText);

            Transport.send(message);
            log.info("✅ Письмо отправлено!");

        } catch (MessagingException e) {
            log.error("❌ Ошибка при отправке письма: {}", e.getMessage());
        }
    }
}
