package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.Investments;
import org.springframework.context.ApplicationEvent;

public class InvestExpirationEvent extends ApplicationEvent {

    private final Investments investments;

    public InvestExpirationEvent(Investments investments) {
        super(investments);

        this.investments = investments;

    }

    public Investments getInvestments() {
        return investments;
    }

}
