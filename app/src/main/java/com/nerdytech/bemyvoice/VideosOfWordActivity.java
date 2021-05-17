package com.nerdytech.bemyvoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class VideosOfWordActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    WordVideoByUserAdapter adapter;
    TextView noVideoAvailabeTV;
    FloatingActionButton add_video;
    String saved_sign_language;
    String initial;
    String word;
    String meaning;
    TextView title;
    ImageView back;
    boolean userVideoAvailable=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_of_word);

        add_video=findViewById(R.id.add_video);
        recyclerView=findViewById(R.id.video_by_user_rv);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        noVideoAvailabeTV=findViewById(R.id.no_video_available_tv);
        title=findViewById(R.id.title);
        back=findViewById(R.id.back);

        Intent intent=getIntent();
        word=intent.getStringExtra("word");
        saved_sign_language = intent.getStringExtra("saved_sign_language");
        meaning = intent.getStringExtra("meaning");
        initial = intent.getStringExtra("initials");
        System.out.println("saved sign language="+saved_sign_language);
        System.out.println("In VideosOfWordActivity\n"+saved_sign_language+"\n"+word+"\n"+meaning);
        title.setText(String.format("Videos of the word %s", word));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CollectionReference colRef = FirebaseFirestore.getInstance().collection("video_dictionary").document(saved_sign_language)
                .collection(initial).document(word).collection("video");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<Video> videoData=new ArrayList<>();
                for (DocumentSnapshot document : value.getDocuments()) {
                    videoData.add(document.toObject(Video.class));
                    if(document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        userVideoAvailable=true;
                    }
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

        add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userVideoAvailable)
                {
                    Toast.makeText(VideosOfWordActivity.this, "You have already uploaded video once for this word\nplease go upload videos for other words or vote for legitimacy of other videos.", Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(VideosOfWordActivity.this, VideoEditAndUploadActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("saved_sign_language", saved_sign_language)
                            .putExtra("word", word)
                            .putExtra("initials", initial)
                            .putExtra("meaning", meaning));
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