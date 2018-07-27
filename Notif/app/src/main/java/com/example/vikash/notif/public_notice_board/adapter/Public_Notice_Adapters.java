package com.example.vikash.notif.public_notice_board.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.notifications.helper.CircleTransform;
import com.example.vikash.notif.public_notice_board.model.PublicNotice;

import java.util.List;

/**
 * Created by vikash on 7/26/18.
 */

public class Public_Notice_Adapters extends RecyclerView.Adapter<Public_Notice_Adapters.MyViewHolder> {
    private Context mContext;
    private List<PublicNotice> noticeList;
    private SparseBooleanArray selectedItems;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView noticeSenderNameTextView,noticeSendingTimestampTextView,noticeTextFeedTextView,noticeLinkTextView;
        public ImageView profilepic, feedImage;

        public MyViewHolder(View view) {
            super(view);
            noticeSenderNameTextView = (TextView) view.findViewById(R.id.noticeSenderNameTextView);
            noticeSendingTimestampTextView = (TextView) view.findViewById(R.id.noticeSendingTimestampTextView);
            noticeTextFeedTextView = (TextView) view.findViewById(R.id.noticeTextFeedTextView);
            noticeLinkTextView = (TextView) view.findViewById(R.id.noticeLinkTextView);
            profilepic = (ImageView) view.findViewById(R.id.profilePic);
            feedImage = (ImageView) view.findViewById(R.id.feedImage);

        }

    }


    public Public_Notice_Adapters(Context mContext, List<PublicNotice> noticeList) {
        this.mContext = mContext;
        this.noticeList = noticeList;
    }

    @Override
    public Public_Notice_Adapters.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed_item, parent, false);

        return new Public_Notice_Adapters.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final Public_Notice_Adapters.MyViewHolder holder, final int position) {
        PublicNotice notice = noticeList.get(position);

        // displaying text view data
        holder.noticeSenderNameTextView.setText(notice.getName());
        holder.noticeSendingTimestampTextView.setText(notice.getTimestamp());
        holder.noticeLinkTextView.setText(notice.getLink());
        if(!notice.getFeed().equals(null))
        {
            holder.noticeTextFeedTextView.setText(notice.getFeed());
        }else{
            holder.noticeTextFeedTextView.setVisibility(View.GONE);
        }
        if(!notice.getLink().equals(null)){
            holder.noticeLinkTextView.setText(notice.getLink());
        }else{
            holder.noticeLinkTextView.setVisibility(View.GONE);
        }

        applyProfilePicture(holder,notice);
        showFeedPic(holder,notice);

    }



    private void applyProfilePicture(Public_Notice_Adapters.MyViewHolder holder, PublicNotice notice) {
        System.out.println("Profile pic method called!");
        if (!notice.getImage().equals(null)) {
            String imgUrl = APIUrl.BASE_URL +"images/"+ notice.getImage();
            System.out.println(imgUrl);

            Glide.with(mContext).load(imgUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.profilepic);
            holder.profilepic.setVisibility(View.VISIBLE);
        } else {
            holder.profilepic.setImageResource(R.drawable.bg_circle);
        }
    }
    private void showFeedPic(Public_Notice_Adapters.MyViewHolder holder, PublicNotice notice) {
        if (!TextUtils.isEmpty(notice.getNotice())) {
            String imageBytes = notice.getNotice();
            byte[] imageByteArray = Base64.decode(imageBytes, Base64.DEFAULT);
            System.out.println(imageBytes);
            Glide.with(mContext).load(imageByteArray).asBitmap().override(325, 449).centerCrop()
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.feedImage);
        } else {
            holder.feedImage.setVisibility(View.GONE);
            }
    }




    @Override
    public long getItemId(int position) {
        return Long.parseLong(noticeList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }
}