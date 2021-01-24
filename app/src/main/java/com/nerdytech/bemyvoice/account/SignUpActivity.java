package com.nerdytech.bemyvoice.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.nerdytech.bemyvoice.MainActivity;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.model.User;
import com.nerdytech.bemyvoice.model.Wallet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    EditText name,email,password,confirm_password;
    Button signup_btn;
    ImageButton google;
    TextView login_existing;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog progressDialog;

    ImageButton visiblity2,visibility3;
    boolean isVisible2=false,isVisible3=false;

    private static final String TAG = "SignUpActivity";
    private static final int RC_SIGN_IN = 9001;
//    GoogleApiClient mGoogleApiClient;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        name=findViewById(R.id.name);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);
        google=findViewById(R.id.google);
        login_existing=findViewById(R.id.existing_acc);
        signup_btn=findViewById(R.id.sign_up_btn);
        visiblity2=findViewById(R.id.visiblity);
        visibility3=findViewById(R.id.visiblity_confirm);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setTitle("Please Wait");

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        visiblity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible2){
                    isVisible2=true;
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visiblity2.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
                else {
                    isVisible2=false;
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visiblity2.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }

            }
        });

        visibility3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible3){
                    isVisible3=true;
                    confirm_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    visibility3.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
                else {
                    isVisible3=false;
                    confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visibility3.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }

            }
        });
        login_existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String uEmail=email.getText().toString();
                String uPass=password.getText().toString();
                final String uConfirmPass=confirm_password.getText().toString();
                final String uName=name.getText().toString();

                boolean flag=true;

                if(TextUtils.isEmpty(uName)){
                    Toast.makeText(SignUpActivity.this, "Enter your name!", Toast.LENGTH_LONG).show();
                    flag=false;
                }
                else if(TextUtils.isEmpty(email.getText()) || !validateEmailAddress(uEmail) ){
                    Toast.makeText(SignUpActivity.this, "Enter Proper Email!", Toast.LENGTH_LONG).show();
                    flag=false;
                }
                else if(TextUtils.isEmpty(password.getText()) || uPass.length()<6 || !passwordValidate(uPass))
                {
                    Toast.makeText(SignUpActivity.this,"Enter password with atleast 6 characters\nIt should have one symbol\nIt should have number\nIt should have one Uppercase chraracter\nIt should have one lowercase character",Toast.LENGTH_LONG).show();
                    flag=false;
                }
                else if(!uConfirmPass.equals(uPass) || TextUtils.isEmpty(confirm_password.getText()))
                {
                    Toast.makeText(SignUpActivity.this, "Password Mismatch!", Toast.LENGTH_LONG).show();
                    flag=false;
                }


                if(flag){
                    mAuth.createUserWithEmailAndPassword(uEmail,uPass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressDialog.dismiss();
                            mUser=mAuth.getCurrentUser();
                            User user=new User(uName,uEmail,mUser.getUid(),"","dd/mm/yy","default");
                            FirebaseDatabase.getInstance().getReference().child("User").child(mUser.getUid())
                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mUser.sendEmailVerification();
                                    System.out.println("email:"+mUser.getEmail());
                                    Toast.makeText(SignUpActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                                    FirebaseDatabase.getInstance().getReference().child("Wallet").child(mUser.getUid()).setValue(new Wallet(500)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i("wallet","created!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i("wallet","not created");
                                        }
                                    });
                                    mAuth.signOut();
                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                }

            }
        });
    }

    private void signIn() {
//        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                authWithGoogle(account);
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                    Log.d("MyTAG", "onComplete: " + (isNew ? "new user" : "old user"));

                    if(isNew)
                    {
                        startActivity(new Intent(getApplicationContext(), SaveUserDataActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                    else {

                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Auth Error",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean passwordValidate(String uPass) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(uPass);

        return matcher.matches();
    }

    private boolean validateEmailAddress(String emailAddress){
        String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }
}