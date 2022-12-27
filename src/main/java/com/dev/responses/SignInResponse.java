package com.dev.responses;

import com.dev.objects.UserObject;

public class SignInResponse extends BasicResponse{
    private String userToken;

    public SignInResponse(boolean success, Integer errorCode, String userToken) {
        super(success, errorCode);
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
