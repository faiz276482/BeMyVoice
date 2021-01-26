package com.nerdytech.bemyvoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.adapter.WordsStartingWithInitialsAdapter;
import com.nerdytech.bemyvoice.model.android.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordStartingWithInitialActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordsStartingWithInitialsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_word_with_initial);
        recyclerView=findViewById(R.id.word_with_initial_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Intent intent=getIntent();
        String initials=intent.getStringExtra("initials");
        String saved_sign_language = intent.getStringExtra("saved_sign_language");
        System.out.println("saved sign language="+saved_sign_language);
        System.out.println("In WordStartingWithIntialActivity\n"+saved_sign_language+"\n"+initials);

        CollectionReference colRef = FirebaseFirestore.getInstance().collection("video_dictionary").document(saved_sign_language)
                .collection(initials);
        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> words = new ArrayList<>();
                    List<Word> wordData=new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        words.add(document.getId());
                        Map<String, Object> map = document.getData();
                        wordData.add(document.toObject(Word.class));
                    }
                    adapter = new WordsStartingWithInitialsAdapter(getBaseContext(),words,wordData);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d("fetched words", words.toString());
                } else {
                    Log.d("fetched words", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}