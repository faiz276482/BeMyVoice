package com.nerdytech.bemyvoice.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainContract;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainPresenter;
import com.nerdytech.bemyvoice.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class SettingsFragment extends Fragment implements MainContract.IView {

    View view;
    private static final long WAIT_TIME = 2000L;
    private static final int TEXT_TO_SPEECH_CODE = 0x100;

    private long TOUCH_TIME = 0;

    private MainContract.IPresenter mPresenter;
    private EditText inputToTranslate;
    private TextView translatedTv;
    private String originalText;
    private String translatedText;
    private boolean connected;

    CloudTTS cloudTTS;
    RelativeLayout neutral,happy,sad,sick,blessed,shocked,angry,celebrate;
    TextView neutralTV,happyTV,sadTV,sickTV,blessedTV,shockedTV,angryTV,celebrateTV,pitchTV,speedTV;
    SeekBar pitch,speed;
    Button testVoice,resetPitchAndSpeed;
    Spinner language,style;
    ArrayAdapter<String> langAdadpter,styleAdadpter;
    String[] voice_style;

    private AdView mAdView;
    View selectedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_settings, container, false);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        neutral=view.findViewById(R.id.neutral);
        happy=view.findViewById(R.id.happy);
        sad=view.findViewById(R.id.sad);
        sick=view.findViewById(R.id.sick);
//        blessed=view.findViewById(R.id.blessed);
        shocked=view.findViewById(R.id.shocked);
        angry=view.findViewById(R.id.angry);
//        celebrate=view.findViewById(R.id.celebrate);
        neutralTV=view.findViewById(R.id.neutral_TV);
        happyTV=view.findViewById(R.id.happy_TV);
        sadTV=view.findViewById(R.id.sad_TV);
        sickTV=view.findViewById(R.id.sick_TV);
//        blessedTV=view.findViewById(R.id.blessed_TV);
        shockedTV=view.findViewById(R.id.shocked_TV);
        angryTV=view.findViewById(R.id.angry_TV);
//        celebrateTV=view.findViewById(R.id.celebrate_TV);
        testVoice=view.findViewById(R.id.test_voice_btn);
        resetPitchAndSpeed=view.findViewById(R.id.reset_pitch_and_speed);
        language=view.findViewById(R.id.select_language_spinner);
        style=view.findViewById(R.id.select_style_spinner);
        pitch=view.findViewById(R.id.pitch_seekbar);
        speed=view.findViewById(R.id.speed_seekbar);
        pitchTV=view.findViewById(R.id.pitch_TV);
        speedTV=view.findViewById(R.id.speed_TV);


        pitch.setMax(4000);
        pitch.setProgress(2000);
        speed.setMax(375);
        speed.setProgress(75);
        cloudTTS=new CloudTTS(view,view.getContext(),getActivity(),"Hello\nThis is a test!");
        loadEmotions();

        mPresenter = new MainPresenter(this);
        mPresenter.onCreate();


        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_chosen_emotion), "neutral");
                editor.apply();
                loadEmotions();
            }
        });
        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_chosen_emotion), "happy");
                editor.apply();
                loadEmotions();
            }
        });
        sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_chosen_emotion), "sad");
                editor.apply();
                loadEmotions();
            }
        });
        sick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_chosen_emotion), "sick");
                editor.apply();
                loadEmotions();
            }
        });
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_chosen_emotion), "angry");
                editor.apply();
                loadEmotions();
            }
        });
        shocked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_chosen_emotion), "shocked");
                editor.apply();
                loadEmotions();
            }
        });

        pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                float pitch = ((float) (progress - 2000) / 10);
                pitchTV.setText("Pitch "+pitch+"x");
                String defaultValue = getResources().getString(R.string.saved_default_emotion);
                String chosenEmotion = sharedPref.getString(getString(R.string.saved_chosen_emotion), defaultValue);
                switch (chosenEmotion) {
                    case "neutral":
                        editor.putInt("neutral_pitch", progress);
                        editor.apply();
//                        Toast.makeText(getContext(), "Neutral Pitch:" + pitch + "x", LENGTH_SHORT).show();

                        break;
                    case "happy":
                        editor.putInt("happy_pitch", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Happy Pitch:" + pitch + "x", LENGTH_SHORT).show();

                        break;
                    case "sad":
                        editor.putInt("sad_pitch", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Sad Pitch:" + pitch + "x", LENGTH_SHORT).show();
                        break;
                    case "sick":
                        editor.putInt("sick_pitch", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Sick Pitch:" + pitch + "x", LENGTH_SHORT).show();
                        break;
                    case "angry":
                        editor.putInt("angry_pitch", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Angry Pitch:" + pitch + "x", LENGTH_SHORT).show();
                        break;
                    case "shocked":
                        editor.putInt("shocked_pitch", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Shocked Pitch:" + pitch + "x", LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                float speed= ((float) (progress + 25) / 100);
                speedTV.setText("Speed "+speed+"x");
                String defaultValue = getResources().getString(R.string.saved_default_emotion);
                String chosenEmotion = sharedPref.getString(getString(R.string.saved_chosen_emotion), defaultValue);
                switch (chosenEmotion) {
                    case "neutral":
                        editor.putInt("neutral_speed", progress);
                        editor.apply();
//                        Toast.makeText(getContext(), "Neutral Speed:" + speed + "x", LENGTH_SHORT).show();

                        break;
                    case "happy":
                        editor.putInt("happy_speed", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Happy Speed:" + speed + "x", LENGTH_SHORT).show();

                        break;
                    case "sad":
                        editor.putInt("sad_speed", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Sad Speed:" + speed + "x", LENGTH_SHORT).show();
                        break;
                    case "sick":
                        editor.putInt("sick_speed", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Sick Speed:" + speed + "x", LENGTH_SHORT).show();
                        break;
                    case "angry":
                        editor.putInt("angry_speed", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Angry Speed:" + speed + "x", LENGTH_SHORT).show();
                        break;
                    case "shocked":
                        editor.putInt("shocked_speed", progress );
                        editor.apply();
//                        Toast.makeText(getContext(), "Shocked Speed:" + speed + "x", LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ArrayList<String> langArray = new ArrayList<>(Languages.voice_lang.keySet());
        Collections.sort(langArray);
        Log.i("Language_Array",langArray.toString());
        //Seting up language Spinner
        langAdadpter=new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,langArray);
        langAdadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(langAdadpter);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_voice_lang_default_key);
        String saved_lang = sharedPref.getString(getString(R.string.saved_voice_lang_key), defaultValue);
        int Position = langAdadpter.getPosition(saved_lang);
        language.setSelection(Position);
        Log.i("Selected_lang",language.getSelectedItem().toString());

        voice_style=Languages.voice.get(Languages.voice_lang.get(language.getSelectedItem().toString()));
        Arrays.sort(voice_style);

        //Seting up language Spinner
        styleAdadpter=new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,voice_style);
        styleAdadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        style.setAdapter(styleAdadpter);
        String defaultValue2 = getResources().getString(R.string.saved_voice_default_key);
        String saved_voice = sharedPref.getString(getString(R.string.saved_voice_key), defaultValue2);
        int Pos= styleAdadpter.getPosition(saved_voice);
        style.setSelection(Pos);

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), (CharSequence) lang1.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_voice_lang_key), String.valueOf(language.getItemAtPosition(position)));
                editor.apply();
                voice_style=Languages.voice.get(Languages.voice_lang.get(language.getItemAtPosition(position).toString()));
                //Seting up language Spinner
                styleAdadpter=new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,voice_style);
                styleAdadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                style.setAdapter(styleAdadpter);
                String defaultValue2 = getResources().getString(R.string.saved_voice_default_key);
                String saved_voice = sharedPref.getString(getString(R.string.saved_voice_key), defaultValue2);
                int Pos= styleAdadpter.getPosition(saved_voice);
                style.setSelection(Pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        style.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_voice_key), String.valueOf(style.getItemAtPosition(position)));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        testVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.startSpeak("Hello!\nThis is a test!");
//                Log.i("ValueOfpitch", String.valueOf(pitch.getProgress()));
//                Log.i("ValueOfSpeed", String.valueOf(speed.getProgress()));
            }
        });

        resetPitchAndSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                String defaultValue = getResources().getString(R.string.saved_default_emotion);
                String chosenEmotion = sharedPref.getString(getString(R.string.saved_chosen_emotion), defaultValue);
                switch (chosenEmotion) {
                    case "neutral":
                        pitch.setProgress(2000);
                        speed.setProgress(75);
                        break;
                    case "happy":
                        pitch.setProgress(2039);
                        speed.setProgress(117);
                        break;
                    case "sad":

                        pitch.setProgress(1026);
                        speed.setProgress(66);
                        break;
                    case "sick":
                        pitch.setProgress(2921);
                        speed.setProgress(69);
                        break;
                    case "angry":
                        pitch.setProgress(1596);
                        speed.setProgress(75);
                        break;
                    case "shocked":
                        pitch.setProgress(2557);
                        speed.setProgress(80);
                        break;
                }

            }
        });


        return view;
    }

    private void loadEmotions() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_default_emotion);
        String chosenEmotion = sharedPref.getString(getString(R.string.saved_chosen_emotion), defaultValue);
        resetEmotionsBG();
        switch (chosenEmotion) {
            case "neutral":
                neutral.setBackgroundResource(R.drawable.emotion_background);
                neutralTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch.setProgress(sharedPref.getInt("neutral_pitch",2000));
                speed.setProgress(sharedPref.getInt("neutral_speed",75));
                break;
            case "happy":
                happy.setBackgroundResource(R.drawable.emotion_background);
                happyTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch.setProgress(sharedPref.getInt("happy_pitch",2039));
                speed.setProgress(sharedPref.getInt("happy_speed",117));
                break;
            case "sad":
                sad.setBackgroundResource(R.drawable.emotion_background);
                sadTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch.setProgress(sharedPref.getInt("sad_pitch",1026));
                speed.setProgress(sharedPref.getInt("sad_speed",66));
                break;
            case "sick":
                sick.setBackgroundResource(R.drawable.emotion_background);
                sickTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch.setProgress(sharedPref.getInt("sick_pitch",2921));
                speed.setProgress(sharedPref.getInt("sick_speed",69));
                break;
            case "angry":
                angry.setBackgroundResource(R.drawable.emotion_background);
                angryTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch.setProgress(sharedPref.getInt("angry_pitch",1596));
                speed.setProgress(sharedPref.getInt("angry_speed",75));
                break;
            case "shocked":
                shocked.setBackgroundResource(R.drawable.emotion_background);
                shockedTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch.setProgress(sharedPref.getInt("shocked_pitch",2557));
                speed.setProgress(sharedPref.getInt("shocked_speed",80));
                break;
        }
        float pitch_ = ((float) (pitch.getProgress() - 2000) / 10);
        pitchTV.setText("Pitch "+pitch_+"x");
        float speed_= ((float) (speed.getProgress() + 25) / 100);
        speedTV.setText("Speed "+speed_+"x");
    }

    private void resetEmotionsBG(){
        neutral.setBackgroundResource(R.drawable.emotion_background2);
        neutralTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
        happy.setBackgroundResource(R.drawable.emotion_background2);
        happyTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
        sad.setBackgroundResource(R.drawable.emotion_background2);
        sadTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
        sick.setBackgroundResource(R.drawable.emotion_background2);
        sickTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
        shocked.setBackgroundResource(R.drawable.emotion_background2);
        shockedTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
        angry.setBackgroundResource(R.drawable.emotion_background2);
        angryTV.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // init android tts
        if (requestCode == TEXT_TO_SPEECH_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mPresenter.initAndroidTTS();
                return;
            }

            Toast.makeText(getContext(),"You do not have the text to speech file you have to install", LENGTH_SHORT);
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        }
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
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_voice_lang_default_key);
        return Languages.voice_lang.get(sharedPref.getString(getString(R.string.saved_voice_lang_key), defaultValue));
    }

    @Override
    public String getSelectedStyleText() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue2 = getResources().getString(R.string.saved_voice_default_key);
        return sharedPref.getString(getString(R.string.saved_voice_key), defaultValue2);
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
        return pitch.getProgress();
    }

    @Override
    public int getProgressSpeakRate() {
        return speed.getProgress();
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
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE);
    }
}