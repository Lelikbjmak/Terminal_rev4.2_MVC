package com.example.Terminal_rev42.EventsListeners;

import com.example.Terminal_rev42.Model.User;
import org.springframework.context.ApplicationEvent;

public class ResetPasswordEvent extends ApplicationEvent {

    private User user;

    private String token;

    private String url;

    public ResetPasswordEvent(User user, String token, String url) {
        super(user);

        this.token = token;
        this.user= user;
        this.url = url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
