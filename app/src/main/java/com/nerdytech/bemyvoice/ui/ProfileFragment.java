package com.nerdytech.bemyvoice.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.account.LoginActivity;
import com.nerdytech.bemyvoice.model.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    View view;

    TextView name,email,dob,mobile;
    CircleImageView profile_image;
    Button logout;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();


        email=view.findViewById(R.id.email);
        name=view.findViewById(R.id.name);
        mobile=view.findViewById(R.id.mobile);
        dob=view.findViewById(R.id.dob);
        profile_image=view.findViewById(R.id.profile_image);
        logout=view.findViewById(R.id.logout_btn);
//        toolbar=view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Profile");

        try{
            if(mUser!=null) {//Checking if user is actually present

                DocumentReference docRef= FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid());
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null)
                        {
                            return;
                        }
                        if (value.exists())
                        {
                            User user=value.toObject(User.class);
                            name.setText(user.getName());
                            email.setText(user.getEmail());
                            mobile.setText(user.getMobile());
                            dob.setText(user.getDob());

                            if(user.getProfile_pic().equals("default")){
                                profile_image.setImageResource(R.mipmap.ic_launcher_round);
                            }
                            else{
                                Picasso.get().load(user.getProfile_pic()).into(profile_image);
                            }

                        }
                    }
                });

            }
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Configure Google Sign In

                try {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                    // [END config_signin]

                    mGoogleSignInClient = GoogleSignIn.getClient(view.getContext(), gso);
                    mGoogleSignInClient.signOut();
                }
                catch (Exception e)
                {
                    Log.i("Logout",e.getMessage());
                }
                Intent intent=new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}