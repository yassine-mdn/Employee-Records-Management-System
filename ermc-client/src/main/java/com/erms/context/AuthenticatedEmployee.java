package com.erms.context;

import com.erms.model.AuthenticationResponse;

public class AuthenticatedEmployee {


    private static AuthenticatedEmployee instance;
    private AuthenticationResponse authenticationResponse;

    private AuthenticatedEmployee() {}


    public static synchronized AuthenticatedEmployee getInstance() {
        if (instance == null) {
            instance = new AuthenticatedEmployee();
        }
        return instance;
    }

    public AuthenticationResponse getAuthenticationResponse() {
        return authenticationResponse;
    }

    public void setAuthenticationResponse(AuthenticationResponse authenticationResponse) {
        this.authenticationResponse = authenticationResponse;
    }
}
