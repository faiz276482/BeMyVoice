package com.nerdytech.bemyvoice.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.nerdytech.bemyvoice.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText email;
    TextView login_existing;
    Button send_email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email);
        send_email=findViewById(R.id.send_email);
        login_existing=findViewById(R.id.login);

        Intent intent=getIntent();
        String emailID=intent.getStringExtra("emailId");

        email.setText(emailID);


        send_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(email.getText().toString());
            }
        });

        login_existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }
}
