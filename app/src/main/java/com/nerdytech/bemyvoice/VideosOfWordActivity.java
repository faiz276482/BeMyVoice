package com.nerdytech.bemyvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.adapter.WordVideoByUserAdapter;
import com.nerdytech.bemyvoice.model.Video;

import java.util.ArrayList;
import java.util.List;

public class VideosOfWordActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordVideoByUserAdapter adapter;
    TextView noVideoAvailabeTV;

    String saved_sign_language;
    String initial;
    String word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_of_word);

        recyclerView=findViewById(R.id.video_by_user_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        noVideoAvailabeTV=findViewById(R.id.no_video_available_tv);

        Intent intent=getIntent();
        word=intent.getStringExtra("word");
        saved_sign_language = intent.getStringExtra("saved_sign_language");
        String meaning = intent.getStringExtra("meaning");
        initial = intent.getStringExtra("initials");
        System.out.println("saved sign language="+saved_sign_language);
        System.out.println("In VideosOfWordActivity\n"+saved_sign_language+"\n"+word+"\n"+meaning);

        CollectionReference colRef = FirebaseFirestore.getInstance().collection("video_dictionary").document(saved_sign_language)
                .collection(initial).document(word).collection("video");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<Video> videoData=new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    videoData.add(document.toObject(Video.class));
                }
                if(videoData.size()>0) {
                    noVideoAvailabeTV.setVisibility(View.INVISIBLE);
                    adapter = new WordVideoByUserAdapter(getBaseContext(), videoData, saved_sign_language);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    String msg=getString(R.string.no_videos_availbale_for_the_word)+"\n"+word;
                    noVideoAvailabeTV.setText(msg);
                    noVideoAvailabeTV.setVisibility(View.VISIBLE);
                }
            }
        });
//        colRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    List<Video> videoData=new ArrayList<>();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        videoData.add(document.toObject(Video.class));
//                    }
//                    adapter = new WordVideoByUserAdapter(getBaseContext(),videoData,saved_sign_language);
//                    recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(VideosOfWordActivity.this,WordStartingWithInitialActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("saved_sign_language",saved_sign_language)
                .putExtra("initials",initial));
        finish();
    }
}