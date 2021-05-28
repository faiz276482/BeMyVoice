package com.nerdytech.bemyvoice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nerdytech.bemyvoice.model.Wallet;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class AddCoinsActivity extends AppCompatActivity implements OnUserEarnedRewardListener {

    private RewardedInterstitialAd rewardedInterstitialAd;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private String TAG = "AddCoinsActivity";
    private RewardedAd mRewardedAd;
    AdRequest adRequest,rewardedInterstitialAdRequest,interstitialAdRequest;
    FirebaseAuth mAuth;
    int coins;
    boolean backPressed=false;
    String from;

    TextView title,coinsTV;
    ImageView back;
    Button interstitial,rewarded_interstitial,rewarded_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coins);

        title=findViewById(R.id.title);
        coinsTV=findViewById(R.id.coins_tv);
        back=findViewById(R.id.back);
        interstitial=findViewById(R.id.btn_interstitial_ad);
        rewarded_video=findViewById(R.id.btn_video_ad);
        rewarded_interstitial=findViewById(R.id.btn_interstitial_reward_ad);

        title.setText(R.string.buy_or_earn_coins);


        mAuth=FirebaseAuth.getInstance();

        coins=Integer.parseInt(getIntent().getStringExtra("coins"));
        from=getIntent().getStringExtra("from");

        coinsTV.setText(String.valueOf(coins));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adRequest = new AdRequest.Builder().build();
        rewardedInterstitialAdRequest = new AdRequest.Builder().build();
        interstitialAdRequest = new AdRequest.Builder().build();
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest2);

        rewarded_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCoinsActivity.this, "Loading Video Ad", LENGTH_LONG).show();
                loadVideoRewardAd();
            }
        });

        rewarded_interstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCoinsActivity.this, "Loading short video Ad", LENGTH_LONG).show();
                loadRewardedInterstictialAd(getString(R.string.rewarded_interstitial_add_id));
            }
        });

        interstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddCoinsActivity.this, "Loading image/video Ad.\nIf video add is shown you may skip.\nYour reward will be added regardless", LENGTH_LONG).show();
                loadInterstitial();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void loadInterstitial(){
        InterstitialAd.load(this,getString(R.string.interstitial_add_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i(TAG, "onAdLoaded");
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(AddCoinsActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        coins+=1;
                        coinsTV.setText(String.valueOf(coins));
                        FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getCurrentUser().getUid())
                                .setValue(new Wallet(coins)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), String.format("%d %s added",1,"coin"), Toast.LENGTH_SHORT).show();
                            }
                        });
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.i(TAG, loadAdError.getMessage());
                Toast.makeText(AddCoinsActivity.this, "Cannot show more than 5 ads of this type in 60 mins", LENGTH_SHORT).show();
                mInterstitialAd = null;
            }
        });
    }

    public void loadVideoRewardAd()
    {
        RewardedAd.load(this, getString(R.string.video_add_id),
                adRequest, new RewardedAdLoadCallback(){
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d(TAG, "Ad was loaded.");
//                        Toast.makeText(AddCoinsActivity.this, "Ad was loaded.", Toast.LENGTH_SHORT).show();
                        if (mRewardedAd != null) {
                            Activity activityContext = AddCoinsActivity.this;
                            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    // Handle the reward.
                                    Log.d(TAG, "The user earned the reward.");
//                                    int rewardAmount = rewardItem.getAmount();
//                                    String rewardType = rewardItem.getType();
                                    addCoins(rewardItem);
//                                    Toast.makeText(activityContext, String.format("%d %s",rewardAmount,rewardType), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d(TAG, "The rewarded ad wasn't ready yet.");
                        }
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad was shown.");
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d(TAG, "Ad failed to show.");
//                                Toast.makeText(AddCoinsActivity.this, "Video Ad failed to show.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Don't forget to set the ad reference to null so you
                                // don't show the ad a second time.
                                Log.d(TAG, "Ad was dismissed.");
//                                Toast.makeText(AddCoinsActivity.this, "Video Ad was dismissed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    public void loadRewardedInterstictialAd(String id) {
        RewardedInterstitialAd.load(AddCoinsActivity.this, id,
                rewardedInterstitialAdRequest,  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialAd.show(AddCoinsActivity.this,AddCoinsActivity.this);
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            /** Called when the ad failed to show full screen content. */
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.i(TAG, "onAdFailedToShowFullScreenContent");
                            }

                            /** Called when ad showed the full screen content. */
                            @Override
                            public void onAdShowedFullScreenContent() {
                                Log.i(TAG, "onAdShowedFullScreenContent");
//                                Toast.makeText(AddCoinsActivity.this, "Watch this add and get 5 coins rewarded", Toast.LENGTH_SHORT).show();
                            }

                            /** Called when full screen content is dismissed. */
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.i(TAG, "onAdDismissedFullScreenContent");
//                                Toast.makeText(AddCoinsActivity.this, "Interstitial Ad was dismissed.", Toast.LENGTH_SHORT).show();
                                if(backPressed) {
                                    if(from!=null) {
                                        startActivity(new Intent(AddCoinsActivity.this, MainActivity.class).putExtra("from",0));
                                        finish();
                                    }
                                    else{
                                        startActivity(new Intent(AddCoinsActivity.this, MainActivity.class).putExtra("from",2));
                                        finish();
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "onAdFailedToLoad");
                        Toast.makeText(AddCoinsActivity.this, "Cannot show more than 5 ads of this type in 15 mins", LENGTH_SHORT).show();
                        if(backPressed) {
                            if(from!=null) {
                                startActivity(new Intent(AddCoinsActivity.this, MainActivity.class).putExtra("from",0));
                                finish();
                            }
                            else{
                                startActivity(new Intent(AddCoinsActivity.this, MainActivity.class).putExtra("from",2));
                                finish();
                            }
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(!backPressed) {
            loadRewardedInterstictialAd(getString(R.string.onback_preesed_rewarded_interstitial_ad));
            Toast.makeText(this, "We will be Showing you a short video ad\nFeel free to skip if you want\nYo will be rewarded 5 coins if you watch it", LENGTH_LONG).show();
            backPressed=true;
        }

    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        addCoins(rewardItem);
        Log.i(TAG,"coins added="+rewardItem.getAmount());
//        Toast.makeText(this, String.format("%d %s added",rewardItem.getAmount(),rewardItem.getType()), Toast.LENGTH_SHORT).show();
    }

    void addCoins(RewardItem rewardItem)
    {
        coins+=rewardItem.getAmount();
        coinsTV.setText(String.valueOf(coins));
        FirebaseDatabase.getInstance().getReference().child("Wallet").child(mAuth.getCurrentUser().getUid())
                .setValue(new Wallet(coins)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "-2 coins for textToSpeech conversion!", LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), String.format("%d %s added",rewardItem.getAmount(),rewardItem.getType()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}