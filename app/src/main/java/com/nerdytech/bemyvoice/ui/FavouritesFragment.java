package com.nerdytech.bemyvoice.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.adapter.FavouritesAdapter;
import com.nerdytech.bemyvoice.model.Favourites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavouritesFragment extends Fragment {

    View view;
    RecyclerView fav_RecyclerView;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;
    FavouritesAdapter adapter;
    TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_favourites, container, false);
        message=view.findViewById(R.id.messages);
        fav_RecyclerView=view.findViewById(R.id.favourites_recycler_view);
        fav_RecyclerView.setHasFixedSize(false);
        fav_RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        DocumentReference docRef=fStore.collection("Favourites").document(mAuth.getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    return;
                }
                if(documentSnapshot.exists()) {
                    message.setVisibility(View.INVISIBLE);
                    fav_RecyclerView.setVisibility(View.VISIBLE);
                    Favourites favourite = documentSnapshot.toObject(Favourites.class);
                    List<String> favourites = favourite.getText();
                    Log.d("favourites",favourites.toString());
                    if (favourites.size() != 0) {

                        adapter = new FavouritesAdapter(view.getContext(),getActivity(),view,favourites);
                        fav_RecyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        message.setVisibility(View.VISIBLE);
                        fav_RecyclerView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        return view;
    }
}