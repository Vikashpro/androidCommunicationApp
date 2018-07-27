package com.example.vikash.notif.conversations.view;

/**
 * Created by vikash on 7/8/18.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;
import com.example.vikash.notif.notifications.model.SendNotice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vikash on 4/29/18.
 */

public class SendNewMessageFragment extends Fragment {
    EditText inputEmail;
    EditText inputSubject;
    EditText inputDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_send_notice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputEmail = (EditText) view.findViewById(R.id.inputEmail);
        inputSubject = (EditText) view.findViewById(R.id.inputSubject);
        inputDescription = (EditText) view.findViewById(R.id.inputDescription);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_send_notice_app_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((Dashboard) getActivity()).getSupportActionBar();

        actionBar.setTitle("Send New Message");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // action with ID action_refresh was selected
            case R.id.action_send:
                sendNewMessage();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(getContext(), "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    private void sendNewMessage(){
        if(inputEmail != null && inputSubject != null && inputDescription != null) {
            String receiver = inputEmail.getText().toString();
            String subject = inputSubject.getText().toString();
            String description = inputDescription.getText().toString();
            String senderEmail = SharedPrefManager.getInstance(getContext()).getUser().getEmail();

            callSendNewMessageAPI(receiver, subject, description, senderEmail);
        }
    }
    private void callSendNewMessageAPI(String receiver,String subject,String description,String senderEmail){
           Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        APIService service = retrofit.create(APIService.class);


        Call<UpdateFlags> call = service.sendNewMessage(receiver,subject,description,senderEmail);

        call.enqueue(new Callback<UpdateFlags>() {
            @Override
            public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {
                if(!response.body().getError()){

                    System.out.println(response.body().getMessage());
                    //Go Back;
                    getFragmentManager().popBackStack();

                }else {
                    System.out.println(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdateFlags> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().popBackStack();
    }
}
