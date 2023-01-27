package com.example.Terminal_rev42.resoursec;

public enum currencyenum {

    USD ("USD"),
    BYN ("BYN"),
    EUR ("EUR"),
    RUB ("RUB");

    private final String currencyName;

    currencyenum(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyName() {
        return currencyName;
    }

}
