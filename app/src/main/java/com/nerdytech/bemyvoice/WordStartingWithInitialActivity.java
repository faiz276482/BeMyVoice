package com.nerdytech.bemyvoice;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.adapter.WordsStartingWithInitialsAdapter;
import com.nerdytech.bemyvoice.model.Word;

import java.text.Normalizer;
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
    private AdView mAdView;
    EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_word_with_initial);
        recyclerView=findViewById(R.id.word_with_initial_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);
        searchEditText=findViewById(R.id.search_edittext);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

                    searchEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            filterAndUpdate(words,wordData,s.toString());
                        }
                    });
                    Log.d("fetched words", words.toString());
                } else {
                    Log.d("fetched words", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void filterAndUpdate(List<String> words, List<Word> wordData,String s) {
        if(!s.equals("")) {
            s=getNormalizedString(s);
            List<String> tempWord = new ArrayList();
            List<Word> tempWordData = new ArrayList();
            for (int i = 0; i < words.size(); i++) {
                String word = getNormalizedString(words.get(i));
                if (word.toLowerCase().contains(s.toLowerCase())) {
                    tempWord.add(word);
                    tempWordData.add(wordData.get(i));
                }
            }
            adapter = new WordsStartingWithInitialsAdapter(getBaseContext(), tempWord, tempWordData, initials, saved_sign_language, null);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        else {
            adapter = new WordsStartingWithInitialsAdapter(getBaseContext(),words,wordData,initials,saved_sign_language,null);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    public String getNormalizedString(String s)
    {
        String normalizedString=Normalizer.normalize(s, Normalizer.Form.NFKD);
        return  normalizedString;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(WordStartingWithInitialActivity.this,MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("from",1));
        finish();
        super.onBackPressed();
    }
}