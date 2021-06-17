package com.nerdytech.bemyvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.adapter.WordsStartingWithInitialsAdapter;
import com.nerdytech.bemyvoice.model.LastLoaded;
import com.nerdytech.bemyvoice.model.Word;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class WordStartingWithInitialActivity extends AppCompatActivity {

    private static final long LIMIT =15 ;
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
    Query next,prev;
    CollectionReference db;
    List<String> words;
    List<Word> wordData;
    static String lastLoaded=null;
    FirebaseAuth mAuth;



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

        db=FirebaseFirestore.getInstance()
                .collection("video_dictionary")
                .document(saved_sign_language)
                .collection(initials);

        mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid())
                .child(saved_sign_language).child(initials).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Query first;
                if(snapshot.exists()) {
                    LastLoaded obj = snapshot.getValue(LastLoaded.class);
                    lastLoaded = obj.getLastLoaded();
//                    Toast.makeText(getApplicationContext(), "Last loaded: "+lastLoaded, Toast.LENGTH_SHORT).show();

                    first=db.orderBy("word")
                            .startAt(lastLoaded)
                            .limit(LIMIT);

                }
                else {
                    first=db.orderBy("word")
                            .limit(LIMIT);

                }
                first.get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                Toast.makeText(WordStartingWithInitialActivity.this, "yo: " + lastLoaded, Toast.LENGTH_SHORT).show();
                                words = new ArrayList<>();
                                wordData = new ArrayList<>();
                                for (QueryDocumentSnapshot document : documentSnapshots) {
                                    words.add(document.getId());
                                    wordData.add(document.toObject(Word.class));
                                }
                                adapter = new WordsStartingWithInitialsAdapter(getBaseContext(), words, wordData, initials, saved_sign_language, null);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                                try {
                                    DocumentSnapshot lastVisible = documentSnapshots.getDocuments()
                                            .get(documentSnapshots.size() - 1);
                                    DocumentSnapshot firstVisible = documentSnapshots.getDocuments()
                                            .get(0);

                                    prev = db.orderBy("word", Query.Direction.DESCENDING)
                                            .startAfter(firstVisible)
                                            .limit(LIMIT);

                                    next = db.orderBy("word")
                                            .startAfter(lastVisible)
                                            .limit(LIMIT);
                                    Log.i("Last visible", lastVisible.toString());
                                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);

                                            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                                Log.d("-----", "end");
                                                try {
                                                    paginate();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                            if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                                                Log.d("-----", "start");
                                                prevpaginate();
                                            }
                                        }
                                    });
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void prevpaginate() {
        prev.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {


                    List<String> tempWords=new ArrayList<>();
                    List<Word> tempWordData=new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        tempWords.add(0,document.getId());
                        tempWordData.add(0,document.toObject(Word.class));
                    }
//                tempWords.remove(tempWords.size()-1);
//                tempWordData.remove(tempWordData.size()-1);
                    tempWords.addAll(words);
                    tempWordData.addAll(wordData);
                    words=tempWords;
                    wordData=tempWordData;
                    adapter = new WordsStartingWithInitialsAdapter(getBaseContext(),words,wordData,initials,saved_sign_language,null);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    int position=task.getResult().size()-2;
                    recyclerView.scrollToPosition(position);

                    Log.d("fetched words", words.toString());

                    try {

                        DocumentSnapshot firstVisible = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                        prev = db.orderBy("word", Query.Direction.DESCENDING)
                                .startAfter(firstVisible)
                                .limit(LIMIT);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        recyclerView.scrollToPosition(0);

                    }
                } else {
                    Log.d("fetched words", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void paginate()
    {
        next.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int position=wordData.size()-11;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        words.add(document.getId());
                        wordData.add(document.toObject(Word.class));
                    }
                    adapter = new WordsStartingWithInitialsAdapter(getBaseContext(),words,wordData,initials,saved_sign_language,null);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(position);

                    Log.d("fetched words", words.toString());
                    try {
                        DocumentSnapshot lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);
                        //                    Toast.makeText(getApplicationContext(), ""+lastVisible, Toast.LENGTH_SHORT).show();
                        Log.i("Last visible",lastVisible.toString());
                        next = db.orderBy("word")

                                .startAfter(lastVisible)
                                .limit(LIMIT);
                    }
                    catch (ArrayIndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                        recyclerView.scrollToPosition(wordData.size()-1);
                    }

                } else {
                    Log.d("fetched words", "Error getting documents: ", task.getException());
                }
            }
        });

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