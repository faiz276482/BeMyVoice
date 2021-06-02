package com.nerdytech.bemyvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.adapter.WordsStartingWithInitialsAdapter;
import com.nerdytech.bemyvoice.model.Word;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class WordsContaingSearchStringActivity extends AppCompatActivity {

    TextView title;
    ImageView back;
    String TAG="WordsContaingSearchStringActivity";
    RecyclerView recyclerView;
    WordsStartingWithInitialsAdapter adapter;
    String search_string;
    String PreferenceKey="beMyVoice";
    String saved_sign_language,alphabets,initials;
    List<String> words;
    List<Word> wordData;
    TextView noWordsAvailableTextView;
    String normalizedSearchString;
    String pattern = "^[A-Za-z0-9.\\-():',+/ ]+$";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_containg_search_string);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        recyclerView=findViewById(R.id.word_with_search_string_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);
        noWordsAvailableTextView=findViewById(R.id.no_word_available_tv);

        words=new ArrayList<>();
        wordData=new ArrayList<>();

        SharedPreferences sharedPreferences=getSharedPreferences(PreferenceKey,MODE_PRIVATE);
        saved_sign_language=sharedPreferences.getString("saved sign language",null);
        alphabets=sharedPreferences.getString("alphabets",null);

        Intent intent=getIntent();
        search_string=intent.getStringExtra("searchString");
        initials=intent.getStringExtra("initials");

        title.setText(String.format("Search Result for words starting with  %s", search_string));
        System.out.println(saved_sign_language+" "+alphabets+" "+search_string+" "+initials);

        normalizedSearchString=getNormalizedString(search_string);

        char initial=normalizedSearchString.charAt(0);
        if((initial+"").matches("ᄋ")&&normalizedSearchString.length()>1){
            System.out.println("ᄋ matches");
            initial=search_string.charAt(0);
        }
        System.out.println("Words starting with "+String.valueOf(initial).toUpperCase());

        CollectionReference colRef= FirebaseFirestore.getInstance()
                .collection(("video_dictionary"))
                .document(saved_sign_language)
                .collection("Words starting with "+String.valueOf(initial).toUpperCase());
        String endcode=normalizedSearchString.substring(0,normalizedSearchString.length()-1)+(char)(normalizedSearchString.charAt(normalizedSearchString.length()-1)+1);
        System.out.println("startcode:"+normalizedSearchString+"\nendcode:"+ endcode);
        colRef.whereGreaterThanOrEqualTo("word",normalizedSearchString).whereLessThan("word",endcode)
//        colRef.whereArrayContains("wordList",normalizedSearchString.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            System.out.println("Size:"+task.getResult().size());
                            for(DocumentSnapshot doc:task.getResult()){
                                Word word = doc.toObject(Word.class);
                                String docId = doc.getId();
                                words.add(docId);
                                wordData.add(word);
                            }
                            if (wordData.size() > 0) {
                                noWordsAvailableTextView.setVisibility(View.INVISIBLE);
                                adapter = new WordsStartingWithInitialsAdapter(getBaseContext(), words, wordData, initials, saved_sign_language, search_string);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                String msg = getString(R.string.no_words_found_containg_search_string) + "\n" + search_string;
                                noWordsAvailableTextView.setText(msg);
                                noWordsAvailableTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        /*
        for(int i=0;i<alphabets.length();i++)
        {
            char ch=alphabets.charAt(i);
            colRef.document(saved_sign_language)
                    .collection("Words starting with "+ch)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(search_string.matches(pattern)) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Word word = documentSnapshot.toObject(Word.class);
                                    String docId = documentSnapshot.getId();

                                    if (docId.toLowerCase().contains(search_string.toLowerCase()) || docId.contains(search_string) || word.getMeaning().toLowerCase().contains(search_string.toLowerCase())) {
                                        words.add(docId);
                                        wordData.add(word);
                                    }
                                }
//                            if(alphabets.charAt(alphabets.length()-1)==ch) {
//                                System.out.println("In " + TAG + ": words" + words.toString());
                                if (wordData.size() > 0) {
                                    noWordsAvailableTextView.setVisibility(View.INVISIBLE);
                                    adapter = new WordsStartingWithInitialsAdapter(getBaseContext(), words, wordData, initials, saved_sign_language, search_string);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    String msg = getString(R.string.no_words_found_containg_search_string) + "\n" + search_string;
                                    noWordsAvailableTextView.setText(msg);
                                    noWordsAvailableTextView.setVisibility(View.VISIBLE);
                                }
//                            }
                            }
                            else{
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    Word word = documentSnapshot.toObject(Word.class);
                                    String docId = documentSnapshot.getId();

                                    if (docId.toLowerCase().contains(search_string.toLowerCase()) || docId.contains(search_string) || word.getMeaning().toLowerCase().contains(search_string.toLowerCase()) || Normalizer.normalize(docId, Normalizer.Form.NFKD).contains(normalizedSearchString)) {
                                        words.add(docId);
                                        wordData.add(word);

                                    }
                                }
//                            if(alphabets.charAt(alphabets.length()-1)==ch) {
//                                System.out.println("In " + TAG + ": words" + words.toString());
                                if (wordData.size() > 0) {
                                    noWordsAvailableTextView.setVisibility(View.INVISIBLE);
                                    adapter = new WordsStartingWithInitialsAdapter(getBaseContext(), words, wordData, initials, saved_sign_language, search_string);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    String msg = getString(R.string.no_words_found_containg_search_string) + "\n" + search_string;
                                    noWordsAvailableTextView.setText(msg);
                                    noWordsAvailableTextView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
//            System.out.println("In "+TAG+": words"+words.toString());
        }*/

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(WordsContaingSearchStringActivity.this,MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("from",1));
        finish();
    }

    public String getNormalizedString(String s)
    {
        String normalizedString=Normalizer.normalize(s, Normalizer.Form.NFKD);
        return  normalizedString;
    }
}