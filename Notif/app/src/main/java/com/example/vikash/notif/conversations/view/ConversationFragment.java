package com.example.vikash.notif.conversations.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.DividerItemDecoration;
import android.widget.Toast;

import com.example.vikash.notif.Dashboard;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.adapter.ConversationAdapter;
import com.example.vikash.notif.conversations.firebaseHelper.Config;
import com.example.vikash.notif.conversations.model.Conversation;
import com.example.vikash.notif.conversations.model.Convos;
import com.example.vikash.notif.conversations.model.Message;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vikash on 6/3/18.
 */

public class ConversationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ConversationAdapter.ConversationAdapterListener {
    private List<Conversation> conversationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ConversationAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private String packageName = "com.example.vikash.notif";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private final String TAG = ConversationFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Conversations");

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.newMessageButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Fragment fragment = null;
        fragment = new SendNewMessageFragment();
        FragmentTransaction ft = ((Dashboard)getActivity()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack("conversation");
        ft.commit();

            }
        });
        //flag of Chatroom ON
        Config.PUSH_TYPE_CHATROOM = true;


        recyclerView = (RecyclerView) view.findViewById(R.id.notificationRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new ConversationAdapter(getContext(), conversationList, this);
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
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
            if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    handlePushNotification(intent);
                }
            }
        };
    }

    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);

        APIService apiService =
                APIUrl.getClient();
        String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
        Call<Convos> call = apiService.getConversations(email);
        call.enqueue(new Callback<Convos>() {
            @Override
            public void onResponse(Call<Convos> call, Response<Convos> response) {
                // clear the inbox
                conversationList.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                List<Conversation> conversations = response.body().getConversation();
                for (Conversation convo : conversations) {
                    // generate a random color
                    convo.setColor(getRandomMaterialColor("400"));
                    conversationList.add(convo);
                }
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Convos> call, Throwable t) {
                Toast.makeText(getContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
     super.onResume();


        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        Config.PUSH_TYPE_CHATROOM = true;
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        // clearing the notification tray
        //NotificationUtils.clearNotifications();
    }

    private void handlePushNotification(Intent intent) {
        String c_id = intent.getStringExtra("c_id");
        boolean c_id_already_exists = false;
        String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
        for (Conversation cr : conversationList) {
        if(cr.getCId().equals(c_id)){
            System.out.println("this is C_Id " + c_id);

            int index = conversationList.indexOf(cr);
            int unReadCounts = cr.getUnreadCount() + 1;
            setConversationIsRead(email,c_id,index,unReadCounts);

               cr.setUnreadCount(unReadCounts);

               conversationList.remove(index);

               conversationList.add(0,cr);

               mAdapter.notifyDataSetChanged();
                c_id_already_exists = true;


               break;

        }

        }
        if(!c_id_already_exists){

        }

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
        Conversation convo = conversationList.get(position);

        String c_id = convo.getCId();
        String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
        setConversationIsImportant(email, c_id, !convo.getIsImportant());
        convo.setIsImportant(!convo.getIsImportant());
        conversationList.set(position, convo);
        mAdapter.notifyDataSetChanged();
    }

    private void setConversationIsImportant(String email, final String c_id, boolean flag) {
        APIService service = APIUrl.getClient();
        Call<UpdateFlags> call = service.setConversationIsImportant(email, c_id, flag);

        call.enqueue(new Callback<UpdateFlags>() {
            @Override
            public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {
                if (response.body().getError()) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT)
                            .show();

                } else {

                }
            }

            @Override
            public void onFailure(Call<UpdateFlags> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });


    }


    @Override
    public void onConversationRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            String c_id = conversationList.get(position).getCId();
            String subject = conversationList.get(position).getSubject();
            String participants = conversationList.get(position).getName();
            if(conversationList.get(position).getUnreadCount() != 0) {
                String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
                setConversationIsRead(email, c_id, position,0);

            }

            Config.PUSH_TYPE_CHATROOM = false;

            MessageFragment messageFragment= new MessageFragment();
            Fragment fragment = null;
            FragmentTransaction ft = ((Dashboard)getActivity()).getSupportFragmentManager().beginTransaction();
            Bundle b = new Bundle();
            b.putString("c_id", c_id);
            b.putString("subject",subject);
            b.putString("participants",participants);
            messageFragment.setArguments(b);
            fragment = messageFragment;
            ft.replace(R.id.content_frame, fragment);
            ft.addToBackStack("conversation");
            ft.commit();

        }

    }



    private void setOnConversationRowClicked(int position, int unReadCounts){
        Conversation convo = conversationList.get(position);
        convo.setUnreadCount(unReadCounts);
        conversationList.set(position, convo);
        mAdapter.notifyDataSetChanged();


    }
    private void setConversationIsRead(String email, final String c_id, final int position, final int unReadCounts) {

        APIService service = APIUrl.getClient();
        Call<UpdateFlags> call = service.setConversationUnReadCounts(email, c_id,unReadCounts);

        call.enqueue(new Callback<UpdateFlags>() {
            @Override
            public void onResponse(Call<UpdateFlags> call, Response<UpdateFlags> response) {
                if (response.body().getError()) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT)
                            .show();
                } else {

                }
            }

            @Override
            public void onFailure(Call<UpdateFlags> call, Throwable t) {
                Log.e(TAG, t.getMessage());
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
                    deleteConversations();
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
        private void deleteConversations() {
            mAdapter.resetAnimationIndex();
            List<Integer> selectedItemPositions =
                    mAdapter.getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                mAdapter.removeData(selectedItemPositions.get(i));
            }
            mAdapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onPause() {
        super.onPause();
       Config.PUSH_TYPE_CHATROOM = false;
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}

