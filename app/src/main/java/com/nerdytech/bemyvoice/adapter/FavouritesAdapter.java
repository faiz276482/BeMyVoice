package com.nerdytech.bemyvoice.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.model.Favourites;
import com.nerdytech.bemyvoice.model.Wallet;
import com.nerdytech.bemyvoice.ui.CloudTTS;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.widget.Toast.LENGTH_SHORT;


public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    Context mContext;
    View view;
    Activity mActivity;
    List<String> listdata;
    CloudTTS cloudTTS;
    int coins;

    FirebaseUser fUser;

    public FavouritesAdapter(Context mContext, Activity mActivity, View view,List<String> listdata,int coins) {
        this.mContext = mContext;
        this.listdata = listdata;
        this.mActivity=mActivity;
        this.view=view;
        this.coins=coins;
        try {
            cloudTTS = new CloudTTS(view, mContext, mActivity, "");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.favourites_listitem,parent,false);
        return new FavouritesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        holder.favouriteText.setText(listdata.get(position));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(coins!=0) {
                    cloudTTS.setText(listdata.get(position));
                    cloudTTS.play();
                    coins -= 2;
                    SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("coins",coins);
                    editor.apply();
                    FirebaseDatabase.getInstance().getReference().child("Wallet").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new Wallet(coins)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext, "-2 coins for textToSpeech conversion!", LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(mContext, "Coins exhausted!\nTo avail this service please Add coins by clicking on the coins button!", LENGTH_SHORT).show();
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listdata.remove(position);
                Favourites fav=new Favourites(listdata);
                FirebaseDatabase.getInstance().getReference().child("Favourites").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(fav);
//                FirebaseFirestore.getInstance().collection("Favourites").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(fav);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayout;
        public TextView favouriteText;
        public ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout=itemView.findViewById(R.id.fav_relative);
            favouriteText=itemView.findViewById(R.id.favourites_TextView);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
