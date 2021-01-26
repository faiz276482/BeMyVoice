package com.nerdytech.bemyvoice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.model.Video;

import java.util.List;

public class WordVideoByUserAdapter extends RecyclerView.Adapter<WordVideoByUserAdapter.ViewHolder>{
    Context mContext;
    String selectedLanguage;
    List<Video> videoData;
    String pattern = "^[A-Za-z0-9. ]+$";


    public WordVideoByUserAdapter(Context mContext, List<Video> videoData,String selectedLanguage) {
        this.mContext = mContext;
        this.videoData=videoData;
        this.selectedLanguage=selectedLanguage;
    }


    @NonNull
    @Override
    public WordVideoByUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.video_by_user_listitem,parent,false);
        return new WordVideoByUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String videoBy="Video by "+videoData.get(position).getAuthor();
        holder.videoByUser.setText(videoBy);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return videoData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayout;
        public TextView videoByUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.video_by_user_rl);
            videoByUser=itemView.findViewById(R.id.video_by_user_TextView);
        }
    }
}
