package com.nerdytech.bemyvoice.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.session.MediaSession;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.nerdytech.bemyvoice.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;


public class BeMyVoiceFragment extends Fragment {

    View view;
    private EditText inputToTranslate;
    private TextView translatedTv;
    private String originalText;
    private String translatedText;
    private boolean connected;
    GoogleCredentials myCredentials;
    RelativeLayout neutral,happy,sad,sick,blessed,shocked,angry,celebrate;
    Spinner lang1,lang2;
    ImageButton exchange;
    FloatingActionButton fabFavourite,fabDelete,fabMic,fabSpeaker;
    ArrayAdapter<String> lang1Adadpter,lang2Adadpter;
    Translate translate;
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

        inputToTranslate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInternetConnection()) {

                    //If there is internet connection, get translate service and start translation:
                    getTranslateService();
                    translate();

                } else {

                    //If not, display "no connection" warning:
                    translatedTv.setText(getResources().getString(R.string.no_connection));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                
            }
        });

        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

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

    public void translate() {

        //Get input text to be translated:
        originalText = inputToTranslate.getText().toString();
        Translation translation = translate.translate(originalText, Translate.TranslateOption.sourceLanguage(Languages.languages.get(lang1.getSelectedItem().toString())),Translate.TranslateOption.targetLanguage(Languages.languages.get(lang2.getSelectedItem().toString())), Translate.TranslateOption.model("base"));
        translatedText = translation.getTranslatedText();

        //Translated text and original text are set to TextViews:
        translatedTv.setText(translatedText);

    }

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }




}
