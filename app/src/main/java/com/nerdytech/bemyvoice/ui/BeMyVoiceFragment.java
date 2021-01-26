package com.nerdytech.bemyvoice.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateScopes;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainContract;
import com.nerdytech.bemyvoice.CloudTextToSpeech.MainPresenter;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.model.Favourites;
import com.nerdytech.bemyvoice.model.Wallet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
    private int pitch;
    private int speed;
    LinearLayout coins;
    TextView coinTV;
    GoogleCredentials myCredentials;
    RelativeLayout neutral,happy,sad,sick,blessed,shocked,angry,celebrate,trnaslateBtn;
    TextView neutralTV,happyTV,sadTV,sickTV,blessedTV,shockedTV,angryTV,celebrateTV;
    Spinner lang1,lang2;
    ImageButton exchange;
    FloatingActionButton fabFavourite,fabDelete,fabMic,fabSpeaker;
    ArrayAdapter<String> lang1Adadpter,lang2Adadpter;
    Translate translate;
//    TextToSpeech t1,t2;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    FirebaseAuth mAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_be_my_voice, container, false);
        mAuth=FirebaseAuth.getInstance();
        inputToTranslate=view.findViewById(R.id.edittext);
        translatedTv=view.findViewById(R.id.edittext2);
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
        lang1=view.findViewById(R.id.lang1);
        lang2=view.findViewById(R.id.lang2);
        exchange=view.findViewById(R.id.exchange);
        fabFavourite=view.findViewById(R.id.fab_favourites);
        fabDelete=view.findViewById(R.id.fab_delete);
        fabMic=view.findViewById(R.id.fab_mic);
        fabSpeaker=view.findViewById(R.id.fab_speaker);
        trnaslateBtn=view.findViewById(R.id.translate_btn);
        coins=view.findViewById(R.id.coins);
        coinTV=view.findViewById(R.id.coins_tv);

        SharedPreferences sharedPreferences=getActivity().getPreferences(Context.MODE_PRIVATE);
        coinTV.setText(String.valueOf(sharedPreferences.getInt("coins",0)));



        mPresenter = new MainPresenter(this);
        mPresenter.onCreate();

        // init android tts
        initAndroidTTS();
        loadEmotions();

        FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Wallet wallet=snapshot.getValue(Wallet.class);
                coinTV.setText(String.valueOf(wallet.getCoins()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                if(!TextUtils.isEmpty(inputToTranslate.getText()))
                {
                    int coinsValue=Integer.parseInt(String.valueOf(coinTV.getText()));
                    if(coinsValue>1) {
                        coinsValue -= 2;
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("coins",coinsValue);
                        editor.apply();
                        FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getCurrentUser().getUid())
                                .setValue(new Wallet(coinsValue)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "-2 coins for textToSpeech conversion!", LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                    Toast.makeText(getContext(), "Coins exhausted!\nTo avail this service please Add coins by clicking on the coins button!", LENGTH_SHORT).show();
                    }
                }

            }
        });


        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputToTranslate.setText("");
                translatedTv.setText("");
            }
        });

        trnaslateBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {


                    final Handler textViewHandler = new Handler();
                    if((lang2.getSelectedItem().equals(lang1.getSelectedItem())) || (lang1.getSelectedItem().toString().equals("English") && lang2.getSelectedItem().toString().equals("Select Language")))
                    {
                        translatedTv.setText(inputToTranslate.getText());
                    }
                    else {
                        int coinsValue = Integer.parseInt(String.valueOf(coinTV.getText()));
                        if(coinsValue>0) {

                            new AsyncTask<Void, Void, Void>() {

                                @SuppressLint("WrongThread")
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    InputStream inputStream = getResources().openRawResource(R.raw.credentials);
                                    GoogleCredentials credentials = null;
                                    try {
                                        credentials = GoogleCredentials.fromStream(inputStream).createScoped(TranslateScopes.all());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("credentials", credentials.toString());
                                    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
                                    // Create the credential
                                    HttpTransport transport = new NetHttpTransport();
                                    JsonFactory jsonFactory = new JacksonFactory();
                                    Translate translate = new Translate.Builder(transport, jsonFactory, requestInitializer)
                                            .setApplicationName("beMyVoice")
                                            .build();

                                    originalText = inputToTranslate.getText().toString();
                                    String target = "en";
                                    if (!lang2.getSelectedItem().equals(R.string.saved_lang2_default_key)) {
                                        target = Languages.languages.get(lang2.getSelectedItem().toString());
                                    }
                                    String source = Languages.languages.get(lang1.getSelectedItem().toString());
                                    String finalTarget = target;


                                    List<String> texts = new LinkedList<>();
                                    texts.add(originalText);
                                    List<TranslationsResource> translationsResourceList =
                                            null;
                                    try {
                                        translationsResourceList = translate
                                                .translations()
                                                .list(texts, target)
                                                .setSource(source)
                                                .setKey(null)
                                                .set("model", null)
                                                .setFormat(null)
                                                .execute()
                                                .getTranslations();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("Translated Text", translationsResourceList.get(0).get("translatedText").toString());
                                    setTranlatedTV(translationsResourceList.get(0).get("translatedText").toString());
                                    if (!TextUtils.isEmpty(inputToTranslate.getText())) {
                                        int coinsValue2 = Integer.parseInt(String.valueOf(coinTV.getText()));
                                        coinsValue2 -= 1;
                                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putInt("coins",coinsValue2);
                                        editor.apply();
                                        FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getCurrentUser().getUid())
                                                .setValue(new Wallet(coinsValue2)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext(), "-1 coin for textTranslation!", LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    return null;
                                }
                            }.execute();
                        }
                        else {
                            Toast.makeText(getContext(), "Coins exhausted!\nTo avail this service please Add coins by clicking on the coins button!", LENGTH_SHORT).show();

                        }
                    }
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

        fabFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int coinsValue = Integer.parseInt(coinTV.getText().toString());
                if (coinsValue > 1) {
                    FirebaseDatabase.getInstance().getReference().child("Favourites").child(mAuth.getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!TextUtils.isEmpty(inputToTranslate.getText())) {
                                        List<String> list;
                                        if(snapshot.exists()) {
                                            Favourites favourites = snapshot.getValue(Favourites.class);
                                            list = (List<String>) favourites.getText();
                                        }
                                        else {
                                            list=new ArrayList<String>();
                                        }
                                        Log.i("list", list.toString());
                                        if (!list.contains(inputToTranslate.getText().toString().toLowerCase())) {
                                            list.add(inputToTranslate.getText().toString().toLowerCase());


                                            FirebaseDatabase.getInstance().getReference().child("Favourites").child(mAuth.getCurrentUser().getUid())
                                                    .setValue(new Favourites(list)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "\"" + inputToTranslate.getText().toString() + "\" added successfully", LENGTH_SHORT).show();
                                                    int coinsValue2=Integer.parseInt(coinTV.getText().toString());
                                                    coinsValue2 -= 2;
                                                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putInt("coins",coinsValue2);
                                                    editor.apply();
                                                    FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .setValue(new Wallet(coinsValue2)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getContext(), "-2 coins for adding to favourites!", LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(getContext(), inputToTranslate.getText().toString() + " already exists in Favourites!", LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Empty text cannot be added in Favourite\nPlease enter some text!", LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
                else {
                    Toast.makeText(getContext(), "Coins exhausted!\nTo avail this service please Add coins by clicking on the coins button!", LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void setTranlatedTV(String translatedText) {
        translatedTv.setText(translatedText);
        this.translatedText=translatedText;
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
                pitch=(sharedPref.getInt("neutral_pitch",2000));
                speed=(sharedPref.getInt("neutral_speed",75));
                break;
            case "happy":
                happy.setBackgroundResource(R.drawable.emotion_background);
                happyTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch=(sharedPref.getInt("happy_pitch",2000));
                speed=(sharedPref.getInt("happy_speed",75));
                break;
            case "sad":
                sad.setBackgroundResource(R.drawable.emotion_background);
                sadTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch=(sharedPref.getInt("sad_pitch",2000));
                speed=(sharedPref.getInt("sad_speed",75));
                break;
            case "sick":
                sick.setBackgroundResource(R.drawable.emotion_background);
                sickTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch=(sharedPref.getInt("sick_pitch",2000));
                speed=(sharedPref.getInt("sick_speed",75));
                break;
            case "angry":
                angry.setBackgroundResource(R.drawable.emotion_background);
                angryTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch=(sharedPref.getInt("angry_pitch",2000));
                speed=(sharedPref.getInt("angry_speed",75));
                break;
            case "shocked":
                shocked.setBackgroundResource(R.drawable.emotion_background);
                shockedTV.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                pitch=(sharedPref.getInt("shocked_pitch",2000));
                speed=(sharedPref.getInt("shocked_speed",75));
                break;
        }
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

    public boolean checkInternetConnection() {

        //Check internet connection:
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }


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
        return pitch;
    }

    @Override
    public int getProgressSpeakRate() {
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
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TEXT_TO_SPEECH_CODE);
    }
}
