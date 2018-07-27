package com.example.vikash.notif.notifications.view;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;
import com.example.vikash.notif.notifications.adapter.MessagesAdapter;
import com.example.vikash.notif.notifications.model.Notice;
import com.example.vikash.notif.notifications.model.Notifications;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vikash on 4/5/18.
 */

public class NotificationFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.NoticeAdapterListener {
    private List<Notice> noticeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private String packageName = "com.example.vikash.notif";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Notifications");

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.newMessageButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = null;
                fragment = new SendNoticeFragment();
                FragmentTransaction ft = ((Dashboard)getActivity()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, fragment);
                ft.commit();

            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.notificationRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MessagesAdapter(getContext(), noticeList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );
    }
    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        APIService apiService =
                APIUrl.getClient();

        Call<Notifications>call = apiService.getNotifications();
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                // clear the inbox
                noticeList.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                List<Notice> notices = response.body().getNotices();
                for (Notice notice : notices) {
                    // generate a random color
                    notice.setColor(getRandomMaterialColor("400"));
                    noticeList.add(notice);
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Notifications> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", packageName);

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }
/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        //ActionBar actionBar = ((Dashboard) getActivity()).getSupportActionBar();



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(getContext(), "Search...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getInbox();
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }
    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Notice notice = noticeList.get(position);

        String notice_id = notice.getNoticeId();
        String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
        setNoticeIsImportant(email,notice_id,!notice.getIsImportant());
        notice.setIsImportant(!notice.getIsImportant());
        noticeList.set(position, notice);
        mAdapter.notifyDataSetChanged();
    }
    private void setNoticeIsImportant(String email, final String notice_id,boolean flag){
        APIService service = APIUrl.getClient();
        Call<UpdateFlags> call = service.setNoticeIsImportant(email,notice_id,flag);

        call.enqueue(new Callback<UpdateFlags>() {
            @Override
            public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {
                if (response.body().getError()) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT)
                            .show();

                }
                else{

                }
            }
            @Override
            public void onFailure(Call<UpdateFlags> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });


    }


    @Override
    public void onNoticeRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
           String notice_id = noticeList.get(position).getNoticeId();
            String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
            setNoticeIsRead(email,notice_id);
            Notice notice = noticeList.get(position);
            notice.setIsRead(true);
            noticeList.set(position, notice);
            mAdapter.notifyDataSetChanged();


            Toast.makeText(getContext(), "Read: " + notice.getDescription(), Toast.LENGTH_SHORT).show();
        }

    }

    private void setNoticeIsRead(String email, final String notice_id){
            APIService service = APIUrl.getClient();
            Call<UpdateFlags> call = service.setNoticeIsRead(email,notice_id);

            call.enqueue(new Callback<UpdateFlags>() {
                @Override
                public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {
                    if (response.body().getError()) {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT)
                                .show();

                    }
                    else{

                    }
                }
                @Override
                public void onFailure(Call<UpdateFlags> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });


    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
    }
    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }
    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    deleteNotices();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }

        // deleting the messages from recycler view
        private void deleteNotices() {
            mAdapter.resetAnimationIndex();
            List<Integer> selectedItemPositions =
                    mAdapter.getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                mAdapter.removeData(selectedItemPositions.get(i));
            }
            mAdapter.notifyDataSetChanged();
        }

    }

    }
