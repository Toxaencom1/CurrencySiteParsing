package com.taxah.currencysiteparsing.service.validator;

import com.taxah.currencysiteparsing.model.AuthData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthDataValidator {
    private final AuthData authData;

    public boolean validateData() {
        if (authData.getUsernameFrom().equals("example@email.com") || authData.getMailPassword().equals("password")) {
            log.warn("Не заполнены данные для отправки письма на почту: {}", authData.getUsernameFrom());
            log.warn("Заполните аутентификационные данные для отправки письма на почту");
            log.warn("Флаги для заполнения: --email.from.user=? --email.from.pass=\"?\" --email.to=?");
            return false;
        } else {
            return true;
        }
    }
}
