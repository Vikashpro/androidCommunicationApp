package com.example.vikash.notif.conversations.view;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.adapter.ConversationAdapter;
import com.example.vikash.notif.conversations.adapter.MessageAdapter;
import com.example.vikash.notif.conversations.firebaseHelper.Config;
import com.example.vikash.notif.conversations.model.Conversation;
import com.example.vikash.notif.conversations.model.Message;
import com.example.vikash.notif.conversations.model.Messages;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vikash on 6/5/18.
 */

public class MessageFragment extends Fragment {

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private EditText sendEditText;
    private Button sendBtn;
    private String global_c_id ;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String TAG = MessageFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messages, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         global_c_id = getArguments().getString("c_id");
        String subject = getArguments().getString("subject");
        String participants = getArguments().getString("participants");
        final String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
        getActivity().setTitle(subject);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(participants);



        sendEditText = (EditText) view.findViewById(R.id.sendEditText);
        sendBtn = (Button) view.findViewById(R.id.sendBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.notificationRecyclerView);

        mAdapter = new MessageAdapter(getContext(), messageList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        getAllMessages(global_c_id);
        sendEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()>0){
                    sendBtn.setEnabled(true);
                }else{
                    sendBtn.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {               //getting text from the textView + names and Contacts from the database and adding
                String message = sendEditText.getText().toString();
                addMessage(message,global_c_id,email);
                sendEditText.setText("");
            }
        });

        Config.PUSH_TYPE_USER = true;
        Config.C_ID =  Integer.parseInt(global_c_id);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.PUSH_MESSAGE_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };



    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }


    private void handlePushNotification(Intent intent){
        String c_id = intent.getStringExtra("c_id");
        String name = intent.getStringExtra("name");
        String sender_email = intent.getStringExtra("sender_email");
        String mess = intent.getStringExtra("message");
        String timestamp = intent.getStringExtra("timestamp");
        String image_url = intent.getStringExtra("image_url");
        System.out.println("gotten here "+ c_id+ "    " + mess + "global c_id " + global_c_id);

        if(c_id.equals(global_c_id)) {
            System.out.println("gotten here "+ c_id+ "    " + mess + "global c_id " + global_c_id);

        Message message = new Message(name,sender_email,image_url,mess,timestamp);
            messageList.add(message);

            mAdapter.notifyDataSetChanged();
        }
    }




    private void getAllMessages(String c_id) {

        APIService apiService =
                APIUrl.getClient();
        Call<Messages> call = apiService.getMessages(c_id);
        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {
                // clear the inbox
                messageList.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                List<Message> Messages = response.body().getMessages();
                for (Message message : Messages) {
                    // generate a random color
                    messageList.add(message);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void addMessage(String message,String c_id,String email){

        APIService apiService =
                APIUrl.getClient();

        Call<UpdateFlags> call = apiService.sendMessage(c_id,message,email);
        call.enqueue(new Callback<UpdateFlags>() {
            @Override
            public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {
                if(!response.body().getError()){
                    System.out.println("message sent!");
                }else{
                    System.out.println("Message Fragment AddMessage() Error: "+ response.body().getError());
                    System.out.println("Error Message : " + response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UpdateFlags> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to Send Message: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Config.PUSH_TYPE_USER = false;
        Config.C_ID = -1;
        System.out.println("onPause Called!");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(null);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().popBackStack();

    }

    @Override
    public void onResume() {
        super.onResume();
        Config.PUSH_TYPE_USER = true;
        Config.C_ID = Integer.parseInt(global_c_id);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_MESSAGE_NOTIFICATION));


    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
