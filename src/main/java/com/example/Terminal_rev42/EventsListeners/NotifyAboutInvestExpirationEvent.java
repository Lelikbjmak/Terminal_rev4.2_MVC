package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Entities.investments;
import org.springframework.context.ApplicationEvent;

public class NotifyAboutInvestExpirationEvent extends ApplicationEvent {

    private investments investments;

    public NotifyAboutInvestExpirationEvent(investments investments) {
        super(investments);

        this.investments = investments;

    }

    public com.example.Terminal_rev42.Entities.investments getInvestments() {
        return investments;
    }

}
