package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Model.user;
import org.springframework.context.ApplicationEvent;

public class MailConfirmationEvent extends ApplicationEvent {

    private String appUrl;
    private com.example.Terminal_rev42.Model.user user;

    public MailConfirmationEvent(user user, String appUrl) {

        super(user);

        this.user = user;

        this.appUrl = appUrl;

    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public com.example.Terminal_rev42.Model.user getUser() {
        return user;
    }

    public void setUser(com.example.Terminal_rev42.Model.user user) {
        this.user = user;
    }
}
