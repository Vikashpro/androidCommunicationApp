package com.example.vikash.notif.updates.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vikash.notif.R;
import com.example.vikash.notif.updates.model.Datesheet;
import com.example.vikash.notif.updates.model.Updates;

import java.util.List;

/**
 * Created by vikash on 6/30/18.
 */

public class DatesheetAdapter extends RecyclerView.Adapter<DatesheetAdapter.MyViewHolder> {
    private Context mContext;
    private List<Updates> updatesList;
    private DatesheetAdapterListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName,textViewDate,textViewLink,textViewCategory;
        public MyViewHolder(View view) {
            super(view);
            textViewName = (TextView) view.findViewById(R.id.textViewName);
            textViewDate = (TextView) view.findViewById(R.id.textViewDate);
            textViewLink = (TextView) view.findViewById(R.id.textViewLink);
            textViewCategory = (TextView) view.findViewById(R.id.textViewCategory);

        }


    }


    public DatesheetAdapter(Context mContext, List<Updates> updatesList, DatesheetAdapterListener listener) {
        this.mContext = mContext;
        this.updatesList = updatesList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_datesheet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Updates update= updatesList.get(position);

        // displaying text view data

        holder.textViewDate.setText(update.getDate());
        holder.textViewName.setText(update.getName());
        SpannableString content = new SpannableString(update.getPdf());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.textViewLink.setText(content);
        holder.textViewCategory.setText(update.getCategory());

        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.textViewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(updatesList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return updatesList.size();
    }


    public interface DatesheetAdapterListener {
        void onIconClicked(int position);


    }
}