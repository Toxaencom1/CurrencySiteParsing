package com.taxah.currencysiteparsing.model;

import lombok.Builder;
import lombok.Data;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Builder
public class Exchanger {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    private static final String href = "https://2gis.kz/almaty/search/";

    private String exchangerInfo;
    private String address;
    private Double usdBuy;
    private Double usdSell;
    private Double eurBuy;
    private Double eurSell;
    private Double rubBuy;
    private Double rubSell;

    @Override
    public String toString() {
        return "\n" +
                "\uD83D\uDFE2➡\uFE0FОбменник: " + exchangerInfo +
                ",\nАдрес: " + address +
                ",\n\uD83D\uDCB2USD: " + usdBuy + " - " + usdSell + "; " +
                "\uD83D\uDCB6EUR: " + eurBuy + " - " + eurSell + "; " +
                GREEN + "₽" + RESET + ": " + rubBuy + " - " + rubSell + "\n" +
                getTwoGisHref() + "\n";
    }

    private String getTwoGisHref() {
        return href + encodeUrl("Обменник " + this.exchangerInfo);
    }

    private String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
        } catch (Exception e) {
            return value.replace(" ", "%20");
        }
    }
}
