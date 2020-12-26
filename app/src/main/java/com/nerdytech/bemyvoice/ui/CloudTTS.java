package com.nerdytech.bemyvoice.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nerdytech.bemyvoice.CloudTextToSpeech.MainContract;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainPresenter;
import com.nerdytech.bemyvoice.R;

import java.util.Objects;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class CloudTTS implements MainContract.IView{
    View view;
    Context mContext;
    Activity activity;
    int pitch,speed;
    private MainContract.IPresenter mPresenter;
    String text;
    private static final int TEXT_TO_SPEECH_CODE = 0x100;


    public CloudTTS(View view, Context mContext,Activity activity,String text) {
        this.view = view;
        this.mContext = mContext;
        this.activity=activity;
        this.text=text;
        mPresenter = new MainPresenter(this);
        mPresenter.onCreate();
        // init android tts
        initAndroidTTS();
        loadEmotion();


    }

    public void setText(String text)
    {
        this.text=text;
    }

    public void loadEmotion(){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = mContext.getResources().getString(R.string.saved_default_emotion);
        String chosenEmotion = sharedPref.getString(mContext.getString(R.string.saved_chosen_emotion), defaultValue);

        switch (chosenEmotion) {
            case "neutral":
                pitch=(sharedPref.getInt("neutral_pitch",2000));
                speed=(sharedPref.getInt("neutral_speed",75));
                break;
            case "happy":
                pitch=(sharedPref.getInt("happy_pitch",2000));
                speed=(sharedPref.getInt("happy_speed",75));
                break;
            case "sad":
                pitch=(sharedPref.getInt("sad_pitch",2000));
                speed=(sharedPref.getInt("sad_speed",75));
                break;
            case "sick":
                pitch=(sharedPref.getInt("sick_pitch",2000));
                speed=(sharedPref.getInt("sick_speed",75));
                break;
            case "angry":
                pitch=(sharedPref.getInt("angry_pitch",2000));
                speed=(sharedPref.getInt("angry_speed",75));
                break;
            case "shocked":
                pitch=(sharedPref.getInt("shocked_pitch",2000));
                speed=(sharedPref.getInt("shocked_speed",75));
                break;
        }
    }

    public  void play()
    {

//        mPresenter.startSpeak(inputToTranslate.getText().toString());
        mPresenter.startSpeak(text);
    }

    @Override
    public void setLanguages(final String[] languages) {
//        if (languages == null) return;
//
//        mAdapterLanguage = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item
//                , languages);
//        mAdapterLanguage.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        mSpinnerLanguage.setAdapter(mAdapterLanguage);
//        mSpinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mPresenter.selectLanguage(languages[position]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }


    @Override
    public void setStyles(final String[] styles) {
//        // init AdapterStyle
//        mAdapterStyle = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item
//                , styles);
//        mAdapterStyle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // init mSpinnerStyle
//        mSpinnerStyle.setAdapter(mAdapterStyle);
//        mSpinnerStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                mPresenter.selectStyle(styles[position]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    @Override
    public void clearUI() {
//        mSpinnerLanguage.setAdapter(null);
//        mSpinnerStyle.setAdapter(null);
//        setTextViewGender("");
//        setTextViewSampleRate("");
    }

    @Override
    public String getSelectedLanguageText() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = activity.getResources().getString(R.string.saved_voice_lang_default_key);
        Log.i("check", Objects.requireNonNull(Languages.voice_lang.get(sharedPref.getString(activity.getString(R.string.saved_voice_lang_key), defaultValue))).toString());
        return Languages.voice_lang.get(sharedPref.getString(activity.getString(R.string.saved_voice_lang_key), defaultValue));
    }

    @Override
    public String getSelectedStyleText() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        String defaultValue2 = activity.getResources().getString(R.string.saved_voice_default_key);
        Log.i("check",sharedPref.getString(activity.getString(R.string.saved_voice_key), defaultValue2));
        return sharedPref.getString(activity.getString(R.string.saved_voice_key), defaultValue2);
    }

    @Override
    public void setTextViewGender(String gender) {
//        mTextViewGender.setText(gender);
    }

    @Override
    public void setTextViewSampleRate(String sampleRate) {
//        mTextViewSampleRate.setText(sampleRate);
    }

    @Override
    public int getProgressPitch() {
//        return 2000;
        return pitch;
    }

    @Override
    public int getProgressSpeakRate() {
//        return 75;
        return speed;
    }

    @Override
    public void makeToast(String text, boolean longShow) {
        Toast.makeText(getContext(), text, (longShow) ? LENGTH_LONG : LENGTH_SHORT).show();
    }

    @Override
    public void invoke(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    @Override
    public void setPresenter(MainContract.IPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return view.getContext();
    }

    private void initAndroidTTS() {
        try {
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            activity.startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
