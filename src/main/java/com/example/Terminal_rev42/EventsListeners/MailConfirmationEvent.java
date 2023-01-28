package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Model.User;
import org.springframework.context.ApplicationEvent;

public class MailConfirmationEvent extends ApplicationEvent {

    private String appUrl;
    private User user;

    public MailConfirmationEvent(User user, String appUrl) {

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
