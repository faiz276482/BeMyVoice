package com.nerdytech.bemyvoice.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.adapter.FavouritesAdapter;
import com.nerdytech.bemyvoice.model.Favourites;
import com.nerdytech.bemyvoice.model.Wallet;

import java.util.List;

public class FavouritesFragment extends Fragment {

    View view;
    RecyclerView fav_RecyclerView;
    FirebaseAuth mAuth;
    FavouritesAdapter adapter;
    TextView message;
    LinearLayout coins;
    TextView coinTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_favourites, container, false);
        message=view.findViewById(R.id.messages);
        fav_RecyclerView=view.findViewById(R.id.favourites_recycler_view);
        fav_RecyclerView.setHasFixedSize(false);
        fav_RecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        mAuth = FirebaseAuth.getInstance();
        coins=view.findViewById(R.id.coins);
        coinTV=view.findViewById(R.id.coins_tv);

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

        FirebaseDatabase.getInstance().getReference().child("Favourites").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            message.setVisibility(View.INVISIBLE);
                            fav_RecyclerView.setVisibility(View.VISIBLE);
                            Favourites favourite = snapshot.getValue(Favourites.class);
                            List<String> favourites = favourite.getText();
                            Log.d("favourites",favourites.toString());
                            if (favourites.size() != 0) {

                                adapter = new FavouritesAdapter(view.getContext(),getActivity(),view,favourites,Integer.parseInt(coinTV.getText().toString()));
                                fav_RecyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                message.setVisibility(View.VISIBLE);
                                fav_RecyclerView.setVisibility(View.INVISIBLE);
                            }
                        }
                        else {
                            message.setVisibility(View.VISIBLE);
                            fav_RecyclerView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return view;
    }
}