package com.example.vikash.notif.conversations.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.conversations.model.Message;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.notifications.helper.CircleTransform;

import java.util.List;

/**
 * Created by vikash on 6/5/18.
 */

public class MessageAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> mMessageList) {
        mContext = context;
        messageList = mMessageList;
    }
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    @Override
    public int getItemViewType(int position) {
        String senderEmail = messageList.get(position).getPEmail();
        String myEmail = SharedPrefManager.getInstance(mContext).getUser().getEmail();

        if (senderEmail.equals(myEmail)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_message, parent, false);
            return new MessageAdapter.SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messageleft, parent, false);
            return new MessageAdapter.ReceivedMessageHolder(view);
        }

        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((MessageAdapter.SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((MessageAdapter.ReceivedMessageHolder) holder).bind(message);
        }
    }
    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.mymessageTextView);
            timeText = (TextView) itemView.findViewById(R.id.mytimeTextView);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());


            long timestamp = java.sql.Timestamp.valueOf(message.getTimestamp()).getTime();

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtils.formatDateTime(mContext, timestamp, DateUtils.FORMAT_ABBREV_MONTH));

        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.pMessageTextView);
            timeText = (TextView) itemView.findViewById(R.id.ptimeTextView);

            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }
        void bind(Message message) {
            messageText.setText(message.getMessage());
            long timestamp = java.sql.Timestamp.valueOf(message.getTimestamp()).getTime();

            // Format the stored timestamp into a readable String using method.
            timeText.setText(DateUtils.formatDateTime(mContext, timestamp, DateUtils.FORMAT_ABBREV_MONTH));


            // Insert the profile image from the URL into the ImageView.
            String imgUrl =  APIUrl.BASE_URL +"images/"+ message.getImage();
            Glide.with(mContext).load(imgUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(profileImage);

        }

    }
}
