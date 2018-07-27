package com.example.vikash.notif.public_notice_board.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.adapter.ConversationAdapter;
import com.example.vikash.notif.conversations.model.Conversation;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;
import com.example.vikash.notif.notifications.model.Notice;
import com.example.vikash.notif.public_notice_board.adapter.Public_Notice_Adapters;
import com.example.vikash.notif.public_notice_board.model.NoticeBoard;
import com.example.vikash.notif.public_notice_board.model.PublicNotice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vikash on 7/25/18.
 */

public class FragmentPublicNoticeBoard extends Fragment {
    private List<PublicNotice> noticeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Public_Notice_Adapters mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.newMessageButton);
        fab.setEnabled(false);
        recyclerView = (RecyclerView) view.findViewById(R.id.notificationRecyclerView);
        mAdapter = new Public_Notice_Adapters(getContext(), noticeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        getPublicNotices();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        ActionBar actionBar = ((Dashboard) getActivity()).getSupportActionBar();

        actionBar.setTitle("Public Notice Board");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // action with ID action_refresh was selected
            case R.id.action_settings:
                Toast.makeText(getContext(), "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    private void getPublicNotices(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        APIService service = retrofit.create(APIService.class);


        Call<NoticeBoard> call = service.getPublicNotices();
        System.out.println("method called!");
        call.enqueue(new Callback<NoticeBoard>() {
            @Override
            public void onResponse(Call<NoticeBoard> call, Response<NoticeBoard> response) {
                if(!response.body().getError()){
                    //Go Back;
                    System.out.println(response.body().getPublicNotices());
                    List<PublicNotice> public_Notices = response.body().getPublicNotices();
                    for (PublicNotice notice: public_Notices) {

                            noticeList.add(notice);
                    }
                    mAdapter.notifyDataSetChanged();

                }else {
                    System.out.println("Error: " +response.body().getErrorMessage());
                }

            }

            @Override
            public void onFailure(Call<NoticeBoard> call, Throwable t) {
                System.out.println("Error " + t.getMessage());
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
        
    }
}