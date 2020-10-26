package com.nerdytech.bemyvoice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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