package com.nerdytech.bemyvoice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nerdytech.bemyvoice.adapter.FavouritesAdapter;
import com.nerdytech.bemyvoice.model.Favourites;
import com.nerdytech.bemyvoice.model.Wallet;
import com.nerdytech.bemyvoice.ui.BeMyVoiceFragment;
import com.nerdytech.bemyvoice.ui.FavouritesFragment;
import com.nerdytech.bemyvoice.ui.ProfileFragment;
import com.nerdytech.bemyvoice.ui.SettingsFragment;
import com.nerdytech.bemyvoice.ui.VideoDictionaryFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth= FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Wallet wallet=snapshot.getValue(Wallet.class);
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("coins",wallet.getCoins());
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Favourites").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> favourites;
                        if(snapshot.exists()) {
                            Favourites favourite = snapshot.getValue(Favourites.class);
                            favourites = favourite.getText();
                        }
                        else {
                            favourites=new ArrayList<>();
                        }
                        SharedPreferences sharedPreferences=getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Set<String> set = new HashSet<String>(favourites);
                        editor.putStringSet("Favourites",set);
                        editor.apply();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        FirebaseFirestore.getInstance().collection("video_dictionary").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPref.edit();
                            String defaultValue = "Select Language";
                            String saved_sign_language = sharedPref.getString("saved sign language", defaultValue);
                            System.out.println("saved sign language="+saved_sign_language);
                            String s="";
                            Set<String> lang=new HashSet<>();
                            lang.add("Select Language");
                            for (QueryDocumentSnapshot doc:task.getResult())
                            {
                                Map<String, Object> map=doc.getData();
//                        System.out.println(doc.getId()+" is approved:"+map.get("approved"));
                                if((boolean)map.get("approved")) {
                                    if(doc.getId().equals(saved_sign_language))
                                    {
                                        editor.putString("alphabets",(String)map.get("alphabets"));
                                    }
                                    lang.add(doc.getId().toString());
                                }
                            }
                            editor.putStringSet("SignLanguages",lang);
                            editor.apply();
                        }
                    }
                });
        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.profile: {
                        selectorFragment = new ProfileFragment();
                        break;
                    }
                    case R.id.settings: {
                        selectorFragment = new SettingsFragment();
                        break;
                    }
                    case R.id.favourites: {
                        selectorFragment = new FavouritesFragment();
                        break;
                    }
                    case R.id.be_my_voice: {
                        selectorFragment = new BeMyVoiceFragment();
                        break;
                    }
                    case R.id.video_dictionary: {
                        selectorFragment = new VideoDictionaryFragment();
                        break;
                    }
                }

                if(selectorFragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();
                }
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new BeMyVoiceFragment()).commit();

    }

}