package com.example.vikash.notif.notifications.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vikash.notif.R;
import com.example.vikash.notif.notifications.model.Notice;

import java.util.List;

/**
 * Created by vikash on 4/6/18.
 */

public class AdaptersExample extends RecyclerView.Adapter<AdaptersExample.NoticeViewHolder> {

    private Context context;
    private List<Notice> noticeList;
    public AdaptersExample(Context context, List<Notice> noticeList){
        this.context = context;
        this.noticeList = noticeList;
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_notice,parent,false);

        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NoticeViewHolder holder, final int position) {
        //getting the notice of the specified position
        Notice notice = noticeList.get(position);

        //binding the data with the viewholder views
        holder.noticeSenderNameTextView.setText(notice.getName());
        holder.noticeSendingTimestampTextView.setText(notice.getDate());
        holder.noticeSubjectTextView.setText(notice.getSubject());
        //holder.noticeDescriptionTextView.setText(notice.getDescription());

        //holder.noticeSenderImageView.setImageDrawable(context.getResources().getDrawable());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {


                }
        });
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }



    class NoticeViewHolder extends RecyclerView.ViewHolder{
       TextView noticeSenderNameTextView, noticeSendingTimestampTextView,noticeSubjectTextView,noticeDescriptionTextView,noticeLinkTextView;
       ImageView noticeSenderProfilePic;
        public NoticeViewHolder(View itemView){
            super(itemView);

            noticeSenderProfilePic = itemView.findViewById(R.id.profilePic);
            noticeSenderNameTextView = itemView.findViewById(R.id.noticeSenderNameTextView);
            noticeSendingTimestampTextView = itemView.findViewById(R.id.noticeSendingTimestampTextView);
            noticeSubjectTextView = itemView.findViewById(R.id.noticeSubjectTextView);
           // noticeDescriptionTextView = itemView.findViewById(R.id.noticeDescriptionTextView);
       //     noticeLinkTextView = itemView.findViewById(R.id.noticeLinkTextView);

        }
    }
}
