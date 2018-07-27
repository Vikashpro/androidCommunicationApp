package com.example.vikash.notif.updates.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.view.ConversationFragment;
import com.example.vikash.notif.notifications.view.NotificationFragment;
import com.example.vikash.notif.updates.adapter.DatesheetAdapter;
import com.example.vikash.notif.updates.model.AttendanceReport;
import com.example.vikash.notif.updates.model.Datesheet;
import com.example.vikash.notif.updates.model.RetrieveUpdates;
import com.example.vikash.notif.updates.model.TimeTable;
import com.example.vikash.notif.updates.model.Updates;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vikash on 7/12/18.
 */

public class DashboardFragment extends Fragment implements View.OnClickListener {

        private ImageButton student,board,messages;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
        savedInstanceState) {
            return inflater.inflate(R.layout.fragment_dashboard, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getActivity().setTitle("Student Dashboard");
            student = (ImageButton) view.findViewById(R.id.studentButton);
            board = (ImageButton) view.findViewById(R.id.boardButton);
            messages = (ImageButton) view.findViewById(R.id.messagesButton);
            student.setOnClickListener(this);
            board.setOnClickListener(this);
            messages.setOnClickListener(this);


        }

    @Override
    public void onClick(View view) {
            Fragment fragment = null;
        if(view == student){
            fragment = new DatesheetFragment();
        }else if(view == board){
            fragment = new NotificationFragment();
        }else if(view == messages){
            fragment = new ConversationFragment();
        }
        if (fragment != null) {
            FragmentTransaction ft = ((Dashboard)getActivity()).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack("Dashboard");
            ft.commit();
        }
    }
}




