package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.investments;
import org.springframework.context.ApplicationEvent;

public class InvestExpirationEvent extends ApplicationEvent {

    private final investments investments;

    public InvestExpirationEvent(investments investments) {
        super(investments);

        this.investments = investments;

    }

    public com.example.Terminal_rev42.Entities.investments getInvestments() {
        return investments;
    }

}
