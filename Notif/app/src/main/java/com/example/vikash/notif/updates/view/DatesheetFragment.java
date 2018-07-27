package com.example.vikash.notif.updates.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
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
 * Created by vikash on 6/30/18.
 */

public class DatesheetFragment extends Fragment  implements DatesheetAdapter.DatesheetAdapterListener{
    private List<Updates> updatesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatesheetAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Updates");
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.newMessageButton);
       SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);
        fab.setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.notificationRecyclerView);
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        recyclerView.addItemDecoration(itemDecorator);

        mAdapter = new DatesheetAdapter(getContext(), updatesList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        getInbox();


    }
    private void getInbox() {

        APIService apiService =
                APIUrl.getClient();
        Call<RetrieveUpdates> call = apiService.getUpdates();
        call.enqueue(new Callback<RetrieveUpdates>() {
            @Override
            public void onResponse(Call<RetrieveUpdates> call, Response<RetrieveUpdates> response) {
                // clear the inbox
                updatesList.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                List<Datesheet> datesheets = response.body().getDatesheets();
                List<TimeTable> timeTables = response.body().getTimeTable();
                List<AttendanceReport> attendanceReports = response.body().getAttendanceReport();
                for (Datesheet datesheet : datesheets) {
                    // generate a random color
                    Updates update = new Updates(datesheet.getDateId(),datesheet.getExam(),datesheet.getDate(),"Exams Datesheet",datesheet.getName());
                    updatesList.add(update);
                }
                for (TimeTable timeTable : timeTables) {
                    // generate a random color
                    Updates update = new Updates(timeTable.getTimeId(),"FICT",timeTable.getDate(),"Time Table", timeTable.getName());
                    updatesList.add(update);
                }
                for (AttendanceReport attendanceReport : attendanceReports) {
                    // generate a random color
                    Updates update = new Updates(attendanceReport.getAId(),attendanceReport.getTeacherName(),attendanceReport.getMonth(),"Attendance Report",attendanceReport.getFile());
                    updatesList.add(update);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<RetrieveUpdates> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onIconClicked(int position) {
        Toast.makeText(getContext(),"hello "+position,Toast.LENGTH_SHORT).show();

        String pdf_url = APIUrl.BASE_URL + "pdfs/hello.pdf";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url));
        startActivity(browserIntent);
    }
}



