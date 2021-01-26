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

import com.google.firebase.auth.FirebaseUser;
import com.nerdytech.bemyvoice.MainActivity;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.WordStartingWithInitialActivity;
import com.nerdytech.bemyvoice.ui.VideoDictionaryFragment;

import java.util.List;


public class WordsStartingWithAdapter extends RecyclerView.Adapter<WordsStartingWithAdapter.ViewHolder> {

    Context mContext;
    List<String> listdata;
    String selectedLanguage;


    FirebaseUser fUser;

    public WordsStartingWithAdapter(Context mContext, List<String> listdata,String selectedLanguage) {
        this.mContext = mContext;
        this.listdata = listdata;
        this.selectedLanguage=selectedLanguage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.words_starting_with_listitem,parent,false);
        return new WordsStartingWithAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


        holder.initiatlsTextView.setText(listdata.get(position));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, WordStartingWithInitialActivity.class).putExtra("saved_sign_language",selectedLanguage)
                        .putExtra("initials",holder.initiatlsTextView.getText().toString())
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                ((MainActivity)mContext).finish();
            }
        });



    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayout;
        public TextView initiatlsTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.initials_rv);
            initiatlsTextView=itemView.findViewById(R.id.initials_TextView);
        }
    }
}