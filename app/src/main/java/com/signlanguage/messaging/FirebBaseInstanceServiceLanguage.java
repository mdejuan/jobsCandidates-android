package com.signlanguage.messaging;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.signlanguage.rest.RestApi;
import com.signlanguage.rest.ServerJsonResponse;

import java.io.IOException;

import retrofit2.Call;

/**
 * Created by marcos on 20/03/18.
 */

public class FirebBaseInstanceServiceLanguage extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken)
    {
        ServerJsonResponse resp = null;
        RestApi restApi = new RestApi();
        RestApi.ClientService service = restApi.getService();
        Call<ServerJsonResponse> call = service.sendIdToken(refreshedToken);
        try {
            resp = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (resp != null && resp.getMessage().equals("ok")) {

        } else {

        }
    }
}
