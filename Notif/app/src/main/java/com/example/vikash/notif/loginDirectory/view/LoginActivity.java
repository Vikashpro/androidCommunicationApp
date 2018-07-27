package com.example.vikash.notif.loginDirectory.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.firebaseHelper.FirebaseInstanceIDService;
import com.example.vikash.notif.loginDirectory.model.Result;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailInput;
    EditText passwordInput;
    Button   buttonSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, Dashboard.class));
        }

        emailInput = (EditText) findViewById(R.id.inputEmail);
        passwordInput = (EditText) findViewById(R.id.inputPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(this);


    }
    private void userSignIn(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<Result> call = service.userLogin(email,password);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if(!response.body().getError()){
                    finish();
                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(response.body().getUser());
                    //Initiating firebase
                    FirebaseMessaging.getInstance().subscribeToTopic("messages");
                    String token = FirebaseInstanceId.getInstance().getToken();
                    String email = SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail();
                    FirebaseInstanceIDService.registerToken(token,email);
                    //end
                    startActivity(new Intent(getApplicationContext(),Dashboard.class));
                }else{
                    Toast.makeText(getApplicationContext(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public void onClick(View view) {
    if(view == buttonSignIn){
        userSignIn();
    }
    }
}
