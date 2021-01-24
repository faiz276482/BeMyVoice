package com.nerdytech.bemyvoice.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.adapter.WordsStartingWithAdapter;
import com.nerdytech.bemyvoice.model.MostLiked;

import java.util.ArrayList;
import java.util.Map;


public class VideoDictionaryFragment extends Fragment {
    View view;
    Spinner signLanguageSpinner;
    ArrayAdapter<String> signLanguageAdpater;
    Button search,addWord;
    EditText search_edittext;
    RecyclerView result;
    WordsStartingWithAdapter adapter;
    ArrayList<String > resultArray;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_video_dictionary, container, false);
//        database_Write();
        signLanguageSpinner=view.findViewById(R.id.sign_language_spinner);
        search_edittext=view.findViewById(R.id.search_edittext);
        result=view.findViewById(R.id.result);
        result.setHasFixedSize(false);
        result.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        addWord=view.findViewById(R.id.add_word_btn);
        search=view.findViewById(R.id.search_btn);

        ArrayList<String> signLanguages=new ArrayList<>();
        signLanguages.add(0,"Select Language");

        FirebaseFirestore.getInstance().collection("video_dictionary").get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    String defaultValue = "Select Language";
                    String saved_sign_language = sharedPref.getString("saved sign language", defaultValue);
                    System.out.println("saved sign language="+saved_sign_language);
                    String s="";
                    for (QueryDocumentSnapshot doc:task.getResult())
                    {
                        Map<String, Object> map=doc.getData();
//                        System.out.println(doc.getId()+" is approved:"+map.get("approved"));
                        if((boolean)map.get("approved")) {
                            if(doc.getId().equals(saved_sign_language))
                            {
                                s=(String)map.get("alphabets");
                            }
                            signLanguages.add(doc.getId().toString());
                        }
                    }
                    //Seting up signLanguage Spinner
                    signLanguageAdpater=new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item,signLanguages);
                    signLanguageAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    signLanguageSpinner.setAdapter(signLanguageAdpater);

                    int Position = signLanguageAdpater.getPosition(saved_sign_language);
                    signLanguageSpinner.setSelection(Position);

                    setResultRecyclerView(s,saved_sign_language);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Something went wrong!\nPlease check your internet connection!", Toast.LENGTH_SHORT).show();
            }
        });


        signLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(), (CharSequence) lang1.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("saved sign language", String.valueOf(signLanguageSpinner.getItemAtPosition(position)));
                editor.apply();
                FirebaseFirestore.getInstance().collection("video_dictionary").document(String.valueOf(signLanguageSpinner.getItemAtPosition(position)))
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map=documentSnapshot.getData();
                        String s=(String)map.get("alphabets");
                        setResultRecyclerView(s, (String) signLanguageSpinner.getItemAtPosition(position));
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void setResultRecyclerView(String s,String selectedLanguage) {
        resultArray=new ArrayList<>();
//        System.out.println(s);
        for(int i=0;i<s.length();i++)
        {
            resultArray.add("Words starting with "+s.charAt(i));
        }

        adapter=new WordsStartingWithAdapter(getContext(),resultArray,selectedLanguage);
        result.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void database_Write() {
//        String s="ᄀᄁᄂᄃᄄᄅᄆᄇᄈᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ" +
//                "아악안알암압앙앞애액앵야얀약양얘" +
//                "어억언얼엄업엉에여역연열염엽영예" +
//                "오옥온올옴옹와완왈왕왜외왼요욕용" +
//                "우욱운울움웅워원월위유육윤율융윷" +
//                "으은을음읍응의이익인일임입잉잎";
        String s="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i=0;i<s.length();i++) {
            System.out.println(s.charAt(i));

            String coll_id="Words starting with "+s.charAt(i);
            String name="British";
            int finalI = i;
            String finalS = s;
            FirebaseFirestore.getInstance().collection("video_dictionary")
                    .document(name+" Sign Language")
                    .collection(coll_id)
                    .document(String.valueOf(s.charAt(i)))
                    .set(new MostLiked("default",0,String.valueOf(s.charAt(i)))).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println(finalS.charAt(finalI)+" is written");
                }
            });
//            FirebaseFirestore.getInstance().collection("video_dictionary")
//                    .document(name+" Sign Language")
//                    .collection(coll_id)
//                    .document(String.valueOf((s.charAt(i))))
//                    .collection(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .document(String.valueOf(s.charAt(i)+"_"+FirebaseAuth.getInstance().getCurrentUser().getUid()))
//                    .set(new Video("default",0,0));
        }
    }
}