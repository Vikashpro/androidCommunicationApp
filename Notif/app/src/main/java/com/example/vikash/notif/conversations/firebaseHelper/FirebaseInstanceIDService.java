package com.example.vikash.notif.conversations.firebaseHelper;


import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.model.Convos;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vikash on 6/11/18.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        String email = SharedPrefManager.getInstance(this).getUser().getEmail();
        if(email != null) {
            registerToken(token, email);
        }
    }
    public static void registerToken(String token,String email){
        //register the token into the mysql/remote database
        //we will do it thru retrofit
        APIService apiService =
                APIUrl.getClient();
            Call<UpdateFlags> call = apiService.registerFirebaseToken(token, email);

            call.enqueue(new Callback<UpdateFlags>() {
                @Override
                public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {

                    // the loop was performed to add colors to each message
                    if (!response.body().getError()) {
                        System.out.println(" Token Registered Successfully " + response.body().getMessage());
                    }
                }

                @Override
                public void onFailure(Call<UpdateFlags> call, Throwable t) {
                    System.out.println("Failure on Registering token " + t.getMessage());
                }
            });

    }


}

