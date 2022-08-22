package com.example.Terminal_rev42.Servicies;

public interface SecurityService {

    String findLoggedInUserName();

    void autoLogin(String login, String password);

    String getAuthenticatedUsername();
}
