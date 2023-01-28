package com.example.Terminal_rev42.Model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Rates {

    private Map<String, Double> rate;

    public Rates(){}

    public Map<String, Double> getRate() {
        return rate;
    }

    public void setRate(Map<String, Double> rate) {
        this.rate = rate;
    }
}
