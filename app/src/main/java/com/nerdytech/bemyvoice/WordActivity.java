package com.nerdytech.bemyvoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nerdytech.bemyvoice.adapter.WordAdapter;
import com.nerdytech.bemyvoice.model.android.Word;

public class WordActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        recyclerView.findViewById(R.id.word_rv);
        SharedPreferences sharedPreferences=getPreferences(Context.MODE_PRIVATE);
        String defaultValue = "Select Language";
        String saved_sign_language = sharedPreferences.getString("saved sign language", defaultValue);
        Intent intent=getIntent();
        String collection_name=intent.getStringExtra("initials");

    }

}