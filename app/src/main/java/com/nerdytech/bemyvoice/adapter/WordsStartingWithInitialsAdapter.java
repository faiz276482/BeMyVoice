package com.nerdytech.bemyvoice.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.model.android.Word;

import java.util.List;


public class WordsStartingWithInitialsAdapter extends RecyclerView.Adapter<WordsStartingWithInitialsAdapter.ViewHolder> {

    Context mContext;
    List<String> words;
    String selectedLanguage;
    List<Word> wordData;
    String pattern = "^[A-Za-z0-9. ]+$";


    public WordsStartingWithInitialsAdapter(Context mContext,List<String> words, List<Word> wordData) {
        this.mContext = mContext;
        this.words = words;
        this.wordData=wordData;
        this.selectedLanguage=selectedLanguage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.words_starting_with_initial_listitem,parent,false);
        return new WordsStartingWithInitialsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(words.get(position).matches(pattern)) {
            holder.word.setText(words.get(position));
        }
        else {
            String word_with_meaning=words.get(position)+" ("+wordData.get(position).getMeaning()+")";
            holder.word.setText(word_with_meaning);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("meaning",wordData.get(position).getMeaning());
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayout;
        public TextView word;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.word_rl);
            word=itemView.findViewById(R.id.word_TextView);
        }
    }
}