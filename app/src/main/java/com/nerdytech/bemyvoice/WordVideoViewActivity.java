package com.nerdytech.bemyvoice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nerdytech.bemyvoice.adapter.WordVideoByUsersAdapter;
import com.nerdytech.bemyvoice.adapter.WordsStartingWithInitialsAdapter;
import com.nerdytech.bemyvoice.model.Video;
import com.nerdytech.bemyvoice.model.Word;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class WordVideoViewActivity extends AppCompatActivity {
    String PreferenceKey="beMyVoice";
    private static final String TAG = "WordVideoViewActivity";
    private Uri uri;

    VideoView videoView;
    Button upvotes,downvotes;
    TextView title;
    ImageView back;

    private MediaController mediaController;

    String saved_sign_language;
    String initial;
    String word;
    String meaning;
    String videoByUsername;
    String videoByUid;
    int upvoteCount,downvoteCount;
    int maxVotes;
    String most_liked;
    String maxVoteVideo;
    String search_string;
    boolean notVoted=true,upVoteState=false,downVoteState=false;
    Video video;
    ImageView deleteVideoBtn;

    StorageReference storageReference;

    ProgressDialog progressDialog;

    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_video_view);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        upvotes=findViewById(R.id.btn_upvotes);
        downvotes=findViewById(R.id.btn_downvotes);
        videoView=findViewById(R.id.video_view);
        title=findViewById(R.id.title);
        deleteVideoBtn = findViewById(R.id.delete_video);
        back=findViewById(R.id.back);

        mediaController = new MediaController(this){
            @Override
            public void show (int timeout){
                if(timeout == 3000) timeout = 1000; //Set to desired number
                super.show(timeout);
            }
        };;
        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        saved_sign_language = intent.getStringExtra("saved_sign_language");
        meaning = intent.getStringExtra("meaning");
        initial = intent.getStringExtra("initials");
        videoByUsername=intent.getStringExtra("username");
        videoByUid=intent.getStringExtra("uid");
        most_liked=intent.getStringExtra("most_liked");
        maxVotes=intent.getIntExtra("maxVotes",0);
        search_string=intent.getStringExtra("searchString");

        if(videoByUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            deleteVideoBtn.setVisibility(View.VISIBLE);
        }

        title.setText(String.format("%s video by %s", word, videoByUsername));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



//        System.out.println("saved sign language=" + saved_sign_language);
        System.out.println("In "+TAG+":\t" + saved_sign_language + "\t" + word + "\t" + meaning +"\t"+ videoByUsername+ "\t"+ videoByUid+"\t"+maxVotes+"\t"+most_liked+"\n"+search_string);



        DocumentReference docRef= FirebaseFirestore.getInstance().collection("video_dictionary")
                .document(saved_sign_language)
                .collection(initial)
                .document(word)
                .collection("video")
                .document(videoByUid);

        deleteVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVideo(docRef,"Your video has been deleted successfully!");
            }
        });

        progressDialog = ProgressDialog.show(WordVideoViewActivity.this,"Loading video","Please Wait!");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                video=documentSnapshot.toObject(Video.class);
                storageReference= FirebaseStorage.getInstance().getReferenceFromUrl(video.getVideo());

                DocumentReference docRef2=docRef.collection("votes").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                if(video.getDownvotes()==0 && video.getUpvotes()==0)
//                {
//                    HashMap<String,String> votes=new HashMap<>();
//                    votes.put("value",null);
//                    docRef2.set(votes);
//                }
                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document=task.getResult();
                            String value=document.getString("value");
//                            System.out.println(TAG+":"+document.getString("upvoted"));
                            if(value==null)
                            {
                                upvotes.setEnabled(true);
                                downvotes.setEnabled(true);
                                notVoted=true;
                            }
                            else if(value.equals("upvote"))
                            {
                                downvotes.setEnabled(true);
                                upvotes.setBackgroundResource(R.drawable.emotion_background);
                                upvotes.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                                notVoted=false;
                            }
                            else if(value.equals("downvote"))
                            {
                                upvotes.setEnabled(true);
                                downvotes.setBackgroundResource(R.drawable.emotion_background);
                                downvotes.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                                notVoted=false;
                            }
                        }

                    }
                });
                upvoteCount= video.getUpvotes();
                downvoteCount=video.getDownvotes();
                upvotes.setText(String.format("%d Upvotes", video.getUpvotes()));
                downvotes.setText(String.format("%d Downvotes", video.getDownvotes()));
                uri=Uri.parse(video.getVideo());
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();
                videoView.setOnPreparedListener(mediaPlayer -> {
                    mediaController.setAnchorView(videoView);
                    progressDialog.dismiss();
                });
//                System.out.println(FirebaseAuth.getInstance().getCurrentUser().getUid());





            }
        });

        upvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Upvote","pressed");

                upvoteCount++;
                if(!notVoted)
                {
                    downvoteCount--;
                    video.setUpvotes(upvoteCount);
                    video.setDownvotes(downvoteCount);
                    docRef.update("upvotes",upvoteCount,"downvotes",downvoteCount);
                    DocumentReference docRef2=docRef.collection("votes").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String,String> votes=new HashMap<>();
                    votes.put("value","upvote");
                    docRef2.set(votes);

                }
                else{
                    video.setUpvotes(upvoteCount);
                    video.setDownvotes(downvoteCount);
                    docRef.update("upvotes",upvoteCount,"downvotes",downvoteCount);
                    DocumentReference docRef2=docRef.collection("votes").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String,String> votes=new HashMap<>();
                    votes.put("value","upvote");
                    docRef2.set(votes);

                }

                if(upvoteCount>maxVotes)
                {
                    DocumentReference docRef2= FirebaseFirestore.getInstance().collection("video_dictionary")
                            .document(saved_sign_language)
                            .collection(initial)
                            .document(word);
                    maxVotes=upvoteCount;
                    System.out.println(TAG+": upvote>maxVote");
                    docRef2.update("votes",upvoteCount,"most_liked",videoByUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            System.out.println(TAG+": MaxVote updated");
                        }
                    });
                }

                upvotes.setText(String.format("%d Upvotes", upvoteCount));
                downvotes.setText(String.format("%d Downvotes", downvoteCount));
                downvotes.setEnabled(true);
                upvotes.setEnabled(false);
                upvotes.setBackgroundResource(R.drawable.emotion_background);
                upvotes.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                downvotes.setBackgroundResource(R.drawable.emotion_background2);
                downvotes.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));
                notVoted=false;

            }
        });

        downvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Downvote","pressed");
                downvoteCount++;
                if(!notVoted)
                {
                    upvoteCount--;
                    video.setUpvotes(upvoteCount);
                    video.setDownvotes(downvoteCount);
                    docRef.update("upvotes",upvoteCount,"downvotes",downvoteCount);
                    DocumentReference docRef2=docRef.collection("votes").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String,String> votes=new HashMap<>();
                    votes.put("value","downvote");
                    docRef2.set(votes);
                    notVoted=false;

                }
                else{
                    video.setUpvotes(upvoteCount);
                    video.setDownvotes(downvoteCount);
                    docRef.update("upvotes",upvoteCount,"downvotes",downvoteCount);
                    DocumentReference docRef2=docRef.collection("votes").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    HashMap<String,String> votes=new HashMap<>();
                    votes.put("value","downvote");
                    docRef2.set(votes);

                }

                double votesPercentage=(upvoteCount/(upvoteCount+downvoteCount))*100;
                if((upvoteCount+downvoteCount)>=5) {
                    if(votesPercentage<=25)
                    {
                        deleteVideo(docRef,"This videos legitimacy score went below 25% so it was deleted!");
                    }
                }

                upvotes.setText(String.format("%d Upvotes", upvoteCount));
                downvotes.setText(String.format("%d Downvotes", downvoteCount));
                upvotes.setEnabled(true);
                downvotes.setEnabled(false);
                downvotes.setBackgroundResource(R.drawable.emotion_background);
                downvotes.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorWhite));
                upvotes.setBackgroundResource(R.drawable.emotion_background2);
                upvotes.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack));
                notVoted=false;

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(WordVideoViewActivity.this, VideosOfWordActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("saved_sign_language", saved_sign_language)
                .putExtra("word", word)
                .putExtra("initials", initial)
                .putExtra("meaning", meaning)
                .putExtra("username",videoByUsername)
                .putExtra("uid",videoByUid)
                .putExtra("maxVotes",maxVotes)
                .putExtra("most_liked",most_liked)
                .putExtra("searchString",search_string));
        finish();

    }

    void deleteVideo(DocumentReference docRef,String msg){
        deleteVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                docRef.collection("votes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                docRef.collection("votes").document(document.getId()).delete();
                                            }
                                            if(most_liked.equals(videoByUid))
                                            {
                                                DocumentReference docRef3= FirebaseFirestore.getInstance().collection("video_dictionary")
                                                        .document(saved_sign_language)
                                                        .collection(initial)
                                                        .document(word);
                                                CollectionReference colRef=docRef3.collection("video");
                                                colRef.orderBy("upvotes", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        if(queryDocumentSnapshots.size()>0) {
                                                            List<Video> video = new ArrayList<>();
                                                            String id = "default";
                                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                                video.add(documentSnapshot.toObject(Video.class));
                                                                id = documentSnapshot.getId();
                                                            }
                                                            System.out.println(video.get(0).getUpvotes() + video.get(0).getAuthor() + id);
                                                            most_liked = id;
                                                            maxVotes = video.get(0).getUpvotes();
//                                                                    if (most_liked == null) {
//                                                                        maxVotes = 0;
//                                                                        most_liked="default";
//                                                                        docRef3.update("votes", 0, "most_liked", "default");
//                                                                    } else {
                                                            docRef3.update("votes", maxVotes, "most_liked", most_liked);
//                                                                    }
                                                        }
                                                        else{
                                                            maxVotes = 0;
                                                            most_liked="default";
                                                            docRef3.update("votes", 0, "most_liked", "default");
                                                        }
                                                    }
                                                });
                                            }
                                            Toast.makeText(WordVideoViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        } else {
                                            Log.d("fetched words", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                            }
                        });

                    }
                });
            }
        });
    }
}