package com.nerdytech.bemyvoice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.cocosw.bottomsheet.BottomSheet;
import com.videotrimmer.library.utils.CompressOption;
import com.videotrimmer.library.utils.LogMessage;
import com.videotrimmer.library.utils.TrimType;
import com.videotrimmer.library.utils.TrimVideo;

import java.io.File;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class VideoEditAndUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_VIDEO = 552;

    private VideoView videoView;
    Uri uri;

    private MediaController mediaController;

    private int trimType;

    String saved_sign_language;
    String initial;
    String word;
    String meaning;
    private static final String TAG = "VideoEditAndUploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit_and_upload);
        videoView = findViewById(R.id.video_view);

        mediaController = new MediaController(this);
        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        saved_sign_language = intent.getStringExtra("saved_sign_language");
        meaning = intent.getStringExtra("meaning");
        initial = intent.getStringExtra("initials");
        System.out.println("saved sign language=" + saved_sign_language);
        System.out.println("In VideosOfWordActivity\n" + saved_sign_language + "\n" + word + "\n" + meaning);

        findViewById(R.id.btn_min_max_gap).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == TrimVideo.VIDEO_TRIMMER_REQ_CODE && data != null) {
                uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data));
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
                    .setCompressOption(new CompressOption(3)) //pass empty constructor for default compress option
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
            Toast.makeText(this, "Upload Clicked", Toast.LENGTH_SHORT).show();
            if(uri!=null)
            {
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(VideoEditAndUploadActivity.this, VideosOfWordActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK)
                .putExtra("saved_sign_language", saved_sign_language)
                .putExtra("word", word)
                .putExtra("initials", initial)
                .putExtra("meaning", meaning));
        finish();
    }

}