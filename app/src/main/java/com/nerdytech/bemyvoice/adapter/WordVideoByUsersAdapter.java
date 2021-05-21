package com.nerdytech.bemyvoice.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.VideoEditAndUploadActivity;
import com.nerdytech.bemyvoice.VideosOfWordActivity;
import com.nerdytech.bemyvoice.WordVideoViewActivity;
import com.nerdytech.bemyvoice.model.Video;
import com.nerdytech.bemyvoice.model.Word;

import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class WordVideoByUsersAdapter extends RecyclerView.Adapter<WordVideoByUsersAdapter.ViewHolder>{
    Context mContext;
    String saved_sign_language;
    List<Video> videoData;
    List<String> uid;
    String word;
    String initial;
    String meaning;
    int maxVotes;
    String most_liked;
    String pattern = "^[A-Za-z0-9.\\-():',+/ ]+$";


    public WordVideoByUsersAdapter(Context mContext, List<Video> videoData, List<String> uid,String saved_sign_language,String word,String initial,String meaning,String most_liked,int maxVotes) {
        this.mContext = mContext;
        this.videoData=videoData;
        this.saved_sign_language=saved_sign_language;
        this.uid=uid;
        this.word=word;
        this.meaning=meaning;
        this.initial=initial;
        this.most_liked=most_liked;
        this.maxVotes=maxVotes;
    }


    @NonNull
    @Override
    public WordVideoByUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.video_by_user_listitem,parent,false);
        return new WordVideoByUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String videoBy="Video by "+videoData.get(position).getAuthor();
        holder.videoByUser.setText(videoBy);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FirebaseFirestore.getInstance().collection("video_dictionary").document(saved_sign_language)
//                        .collection(initial).document(word).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        Word word=documentSnapshot.toObject(Word.class);
//                        maxVotes=word.getVotes();
//                        most_liked=word.getMost_liked();
//                        System.out.println("maxVotes:"+maxVotes+"\nmost_like:"+most_liked);
//                    }
//                });
                mContext.startActivity(new Intent(mContext, WordVideoViewActivity.class)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK )
                        .putExtra("saved_sign_language", saved_sign_language)
                        .putExtra("word", word)
                        .putExtra("initials", initial)
                        .putExtra("meaning", meaning)
                        .putExtra("username",videoData.get(position).getAuthor())
                        .putExtra("uid",uid.get(position))
                        .putExtra("maxVotes",maxVotes).putExtra("most_liked",most_liked));
//                ((VideosOfWordActivity)mContext).finish();
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
