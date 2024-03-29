package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Model.VerificationToken;
import com.example.Terminal_rev42.Model.User;
import org.springframework.context.ApplicationEvent;

public class MailConfirmationResendEvent extends ApplicationEvent {

    private String appUrl;

    private User user;

    private VerificationToken token;

    public MailConfirmationResendEvent(User user, String appURL, VerificationToken token) {

        super(user);
        this.user = user;
        this.appUrl = appURL;
        this.token = token;

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

    public VerificationToken getToken() {
        return token;
    }

    public void setToken(VerificationToken token) {
        this.token = token;
    }
}
