package com.nerdytech.bemyvoice.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainContract;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainPresenter;
import com.nerdytech.bemyvoice.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;


public class BeMyVoiceFragment extends Fragment implements MainContract.IView{

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
    GoogleCredentials myCredentials;
    RelativeLayout neutral,happy,sad,sick,blessed,shocked,angry,celebrate,trnaslateBtn;
    Spinner lang1,lang2;
    ImageButton exchange;
    FloatingActionButton fabFavourite,fabDelete,fabMic,fabSpeaker;
    ArrayAdapter<String> lang1Adadpter,lang2Adadpter;
    Translate translate;
//    TextToSpeech t1,t2;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_be_my_voice, container, false);
        inputToTranslate=view.findViewById(R.id.edittext);
        translatedTv=view.findViewById(R.id.edittext2);
        neutral=view.findViewById(R.id.neutral);
        happy=view.findViewById(R.id.happy);
        sad=view.findViewById(R.id.sad);
        sick=view.findViewById(R.id.sick);
        blessed=view.findViewById(R.id.blessed);
        shocked=view.findViewById(R.id.shocked);
        angry=view.findViewById(R.id.angry);
        celebrate=view.findViewById(R.id.celebrate);
        lang1=view.findViewById(R.id.lang1);
        lang2=view.findViewById(R.id.lang2);
        exchange=view.findViewById(R.id.exchange);
        fabFavourite=view.findViewById(R.id.fab_favourites);
        fabDelete=view.findViewById(R.id.fab_delete);
        fabMic=view.findViewById(R.id.fab_mic);
        fabSpeaker=view.findViewById(R.id.fab_speaker);
        trnaslateBtn=view.findViewById(R.id.translate_btn);

        mPresenter = new MainPresenter(this);
        mPresenter.onCreate();

        // init android tts
        initAndroidTTS();

        ArrayList<String> langArray = new ArrayList<>(Languages.languages.keySet());
        Collections.sort(langArray);
        langArray.add(0,"Select Language");

        //Seting up lang1 Spinner
        lang1Adadpter=new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,langArray);
        lang1Adadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang1.setAdapter(lang1Adadpter);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_lang1_default_key);
        String saved_lang1 = sharedPref.getString(getString(R.string.saved_lang1_key), defaultValue);
        int Position = lang1Adadpter.getPosition(saved_lang1);
        lang1.setSelection(Position);

        //Setting up lang2 Spinner
        lang2Adadpter=new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,langArray);
        lang2Adadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang2.setAdapter(lang2Adadpter);

        String defaultValue2 = getResources().getString(R.string.saved_lang2_default_key);
        String saved_lang2 = sharedPref.getString(getString(R.string.saved_lang2_key), defaultValue2);
        int Pos = lang2Adadpter.getPosition(saved_lang2);
        lang2.setSelection(Pos);

        //String selectedItem = (String) lang1.getSelectedItem();

        lang1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), (CharSequence) lang1.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_lang1_key), String.valueOf(lang1.getItemAtPosition(position)));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lang2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_lang2_key), String.valueOf(lang2.getItemAtPosition(position)));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp= (String) lang1.getSelectedItem();
                lang1.setSelection(lang2Adadpter.getPosition((String) lang2.getSelectedItem()));
                lang2.setSelection(lang1Adadpter.getPosition(temp));
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_lang2_key), String.valueOf(lang2.getSelectedItem()));
                editor.putString(getString(R.string.saved_lang1_key), String.valueOf(lang1.getSelectedItem()));
                editor.apply();
                inputToTranslate.setText(translatedText);
                translatedTv.setText(originalText);
                originalText=translatedText;
                translatedText=translatedTv.getText().toString();
            }
        });

        fabSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.startSpeak(inputToTranslate.getText().toString());
            }
        });

//        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    t1.setLanguage(Locale.forLanguageTag(Languages.languages.get(lang1.getSelectedItem().toString())));
//                }
//            }
//        });
//
//        t2=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status != TextToSpeech.ERROR) {
//                    t2.setLanguage(Locale.forLanguageTag(Languages.languages.get(lang2.getSelectedItem().toString())));
//                }
//            }
//        });
//
//        fabSpeaker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String toSpeak = inputToTranslate.getText().toString();
//                Toast.makeText(getContext(), toSpeak,Toast.LENGTH_SHORT).show();
//                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//            }
//        });

//        inputToTranslate.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputToTranslate.setText("");
                translatedTv.setText("");
            }
        });

        trnaslateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {

                    //If there is internet connection, get translate service and start translation:
                    getTranslateService();
                    translate();

                } else {

                    //If not, display "no connection" warning:
                    translatedTv.setText(getResources().getString(R.string.no_connection));
                }
            }
        });

        fabMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        return view;
    }

    //Funtions for Language translation

    public void getTranslateService() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
             myCredentials = GoogleCredentials.fromStream(is);



            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();

        }
    }

    public void tts(){

    }

    public void translate() {

        //Get input text to be translated:

        if((lang2.getSelectedItem().equals(lang1.getSelectedItem())) || (lang1.getSelectedItem().toString().equals("English") && lang2.getSelectedItem().toString().equals("Select Language")))
        {
            translatedTv.setText(inputToTranslate.getText());
        }
        else {

            originalText = inputToTranslate.getText().toString();
            Translate.TranslateOption toBeTranslatedLanguage = Translate.TranslateOption.targetLanguage("en");
            if (!lang2.getSelectedItem().equals(R.string.saved_lang2_default_key)) {
                toBeTranslatedLanguage = Translate.TranslateOption.targetLanguage(Languages.languages.get(lang2.getSelectedItem().toString()));
            }
            Translation translation = translate.translate(originalText, Translate.TranslateOption.sourceLanguage(Languages.languages.get(lang1.getSelectedItem().toString())), toBeTranslatedLanguage, Translate.TranslateOption.model("base"));
            translatedText = translation.getTranslatedText();

            //Translated text and original text are set to TextViews:
            translatedTv.setText(translatedText);
        }

    }

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }
//    public void onPause(){
//        if(t1 !=null){
//            t1.stop();
////            t1.shutdown();
//        }
//        super.onPause();
//    }

    //Functions for Speech To Text conversion

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Languages.languages.get(lang1.getSelectedItem().toString()));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say something!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                inputToTranslate.setText(result.get(0));
            }
        }
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
        return "en-GB";
    }

    @Override
    public String getSelectedStyleText() {
        return "en-GB-Wavenet-F";
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
        return 2000;
    }

    @Override
    public int getProgressSpeakRate() {
        return 75;
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
