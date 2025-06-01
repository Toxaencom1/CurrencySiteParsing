package com.taxah.currencysiteparsing.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthData {
    private String usernameFrom;
    private String mailPassword;
    private String emailRecipients;
    private String topicName;
}
