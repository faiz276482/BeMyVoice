package com.nerdytech.bemyvoice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.nerdytech.bemyvoice.model.Word;

import java.util.ArrayList;
import java.util.List;

public class WordStartingWithInitialActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordsStartingWithInitialsAdapter adapter;
    String initials;
    String saved_sign_language;
    TextView title;
    ImageView back;
    int maxVotes;
    String most_liked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_word_with_initial);
        recyclerView=findViewById(R.id.word_with_initial_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);


        Intent intent=getIntent();
        initials=intent.getStringExtra("initials");
        saved_sign_language = intent.getStringExtra("saved_sign_language");
        System.out.println("saved sign language="+saved_sign_language);
        System.out.println("In WordStartingWithIntialActivity\n"+saved_sign_language+"\n"+initials);
        title.setText(initials);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                        wordData.add(document.toObject(Word.class));
                    }
                    adapter = new WordsStartingWithInitialsAdapter(getBaseContext(),words,wordData,initials,saved_sign_language,null);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    Log.d("fetched words", words.toString());
                } else {
                    Log.d("fetched words", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(WordStartingWithInitialActivity.this,MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("from",1));
        finish();
        super.onBackPressed();
    }
}