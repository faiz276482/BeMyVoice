package com.nerdytech.bemyvoice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nerdytech.bemyvoice.model.User;
import com.nerdytech.bemyvoice.model.Video;
import com.videotrimmer.library.utils.CompressOption;
import com.videotrimmer.library.utils.LogMessage;
import com.videotrimmer.library.utils.TrimType;
import com.videotrimmer.library.utils.TrimVideo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class VideoEditAndUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_VIDEO = 552;
    private static final int REQUEST_UPLOAD = 101;

    StorageReference sRef;
    String author;

    private VideoView videoView;
    private TextView progressBar_tv;
    Uri uri;
    String url;

    private MediaController mediaController;

    private int trimType;
    private ProgressBar progressBar;

    String saved_sign_language;
    String initial;
    String word;
    String meaning;
    String PreferenceKey="beMyVoice";
    private static final String TAG = "VideoEditAndUploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit_and_upload);
        videoView = findViewById(R.id.video_view);
        progressBar=findViewById(R.id.progressBar);
        progressBar_tv=findViewById(R.id.progressBar_tv);

        mediaController = new MediaController(this);
        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        saved_sign_language = intent.getStringExtra("saved_sign_language");
        meaning = intent.getStringExtra("meaning");
        initial = intent.getStringExtra("initials");
        System.out.println("saved sign language=" + saved_sign_language);
        System.out.println("In "+TAG+":\t" + saved_sign_language + ":\t" + word + ":\t" + meaning);
        sRef= FirebaseStorage.getInstance().getReference().child("videos")
                .child(saved_sign_language)
                .child(initial).child(word)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()+".mp4");

        findViewById(R.id.btn_min_max_gap).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
    }

    public String getAuthorname(String uid){
        SharedPreferences sharedPreferences=getSharedPreferences(PreferenceKey,Context.MODE_PRIVATE);
        author=sharedPreferences.getString("name","");
        return author;

    }

    public void updateFireStore(String url)
    {
        String upload_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        author=getAuthorname(uid);
        Video video=new Video(url,author,upload_date,0,0);

        DocumentReference docRef= FirebaseFirestore.getInstance().collection("video_dictionary")
                .document(saved_sign_language)
                .collection(initial)
                .document(word)
                .collection("video")
                .document(uid);
        docRef.set(video)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        goBackToPrevActivity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getLocalizedMessage());
            }
        });
    }

    private void goBackToPrevActivity() {
        startActivity(new Intent(VideoEditAndUploadActivity.this, VideosOfWordActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK)
                .putExtra("saved_sign_language", saved_sign_language)
                .putExtra("word", word)
                .putExtra("initials", initial)
                .putExtra("meaning", meaning));
        finish();
    }

    public void upload()
    {
        if(uri!=null)
        {

            progressBar.setVisibility(View.VISIBLE);
            progressBar_tv.setVisibility(View.VISIBLE);
            Log.i("URI",uri.getPath());
            UploadTask uploadTask=sRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(VideoEditAndUploadActivity.this, "Upload Failed:"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(VideoEditAndUploadActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar_tv.setVisibility(View.INVISIBLE);

                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url =uri.toString();
                            updateFireStore(url);
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    updateUploadProgress(snapshot);
                }
            });
        }
        else{
            Toast.makeText(this, "Video Uri is null: please choose a video first!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    private void updateUploadProgress(UploadTask.TaskSnapshot snapshot) {
        long fileSize=snapshot.getTotalByteCount();
        long uploadBytes=snapshot.getBytesTransferred();
        long progress=(100*uploadBytes)/fileSize;

        progressBar.setProgress((int) progress);
        progressBar_tv.setText(String.format("%d%%", progress));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
                uri = Uri.fromFile(new File(TrimVideo.getTrimmedVideoPath(data)));
                Log.d(TAG, "Trimmed path:: " + uri);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();
                findViewById(R.id.btn_upload).setEnabled(true);

                videoView.setOnPreparedListener(mediaPlayer -> {
                    mediaController.setAnchorView(videoView);
                });

                String filepath = String.valueOf(uri);
                File file = new File(filepath);
//                uri=Uri.fromFile(file);
                long length = file.length();
                Log.d(TAG, "Video size:: " + (length / 1024));
            } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
            /*    //check video duration if needed
                if (TrimmerUtils.getVideoDuration(this,data.getData())<=30){
                    Toast.makeText(this,"Video should be larger than 30 sec",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (data.getData() != null) {
                    LogMessage.v("Video path:: " + data.getData());
                    openTrimActivity(String.valueOf(data.getData()));
                } else {
                    Toast.makeText(this, "video uri is null", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTrimActivity(String data) {
        if (trimType == 3) {
            LogMessage.v("Valasdd "+data);
            TrimVideo.activity(data)
                    .setTrimType(TrimType.MIN_MAX_DURATION)
                    .setMinToMax(2, 5)
                    .setHideSeekBar(true)
                    .setCompressOption(new CompressOption(5)) //pass empty constructor for default compress option
                    .setDestination("/storage/emulated/0/DCIM/TESTFOLDER")
                    .start(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_min_max_gap) {
            onMinToMaxTrimClicked();
        }
        else if (v.getId() == R.id.btn_upload) {
//            Toast.makeText(this, "Upload Clicked", Toast.LENGTH_SHORT).show();
            if(uri!=null)
            {
//                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
                upload();
            }
        }
    }



    private void onMinToMaxTrimClicked() {
        trimType = 3;
        if (checkCamStoragePer())
            showVideoOptions();
    }

    public void showVideoOptions() {
        try {
            BottomSheet.Builder builder = getBottomSheet();
            builder.sheet(R.menu.menu_video);
            builder.listener(item -> {
                if (R.id.action_take == item.getItemId())
                    captureVideo();
                else
                    openVideo();
                return false;
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BottomSheet.Builder getBottomSheet() {
        return new BottomSheet.Builder(this).title(R.string.txt_option);
    }

    public void captureVideo() {
        try {
            Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
            intent.putExtra("android.intent.extra.durationLimit", 30);
            startActivityForResult(intent, REQUEST_TAKE_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openVideo() {
        try {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_VIDEO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isPermissionOk(grantResults))
            showVideoOptions();
    }

    private boolean isEdtTxtEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private long getEdtValueLong(EditText editText) {
        return Long.parseLong(editText.getText().toString().trim());
    }

    private boolean checkCamStoragePer() {
        return checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
    }

    private boolean checkPermission(String... permissions) {
        boolean allPermitted = false;
        for (String permission : permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED);
            if (!allPermitted)
                break;
        }
        if (allPermitted)
            return true;
        ActivityCompat.requestPermissions(this, permissions,
                220);
        return false;
    }

    private boolean isPermissionOk(int... results) {
        boolean isAllGranted = true;
        for (int result : results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false;
                break;
            }
        }
        return isAllGranted;
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
        goBackToPrevActivity();
    }

}