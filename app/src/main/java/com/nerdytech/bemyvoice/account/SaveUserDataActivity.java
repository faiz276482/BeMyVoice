package com.nerdytech.bemyvoice.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nerdytech.bemyvoice.MainActivity;
import com.nerdytech.bemyvoice.R;
import com.nerdytech.bemyvoice.model.Favourites;
import com.nerdytech.bemyvoice.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SaveUserDataActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    EditText name,email,dob,mobile;
    Button save;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_user_data);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        dob=findViewById(R.id.dob);
        save=findViewById(R.id.save);

        email.setText(mUser.getEmail());
        name.setText(mUser.getDisplayName());
        mobile.setText(mUser.getPhoneNumber());

        myCalendar= Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(SaveUserDataActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName=name.getText().toString();
                String uEmail=email.getText().toString();
                String uMob=mobile.getText().toString();
                String uDob=dob.getText().toString();

                boolean temp=true;

                if(TextUtils.isEmpty(email.getText())){
                    Toast.makeText(SaveUserDataActivity.this, "Enter Proper Email", Toast.LENGTH_SHORT).show();
                    temp=false;
                }
                else if(TextUtils.isEmpty(name.getText())){
                    Toast.makeText(SaveUserDataActivity.this, "Enter Proper Name", Toast.LENGTH_SHORT).show();
                    temp=false;
                }
                else if(TextUtils.isEmpty(mobile.getText())){
                    Toast.makeText(SaveUserDataActivity.this, "Enter Proper Mobile No.", Toast.LENGTH_SHORT).show();
                    temp=false;
                }
                else if(TextUtils.isEmpty(dob.getText())){
                    Toast.makeText(SaveUserDataActivity.this, "Enter Proper DOB", Toast.LENGTH_SHORT).show();
                    temp=false;
                }

                if(temp)
                {
                    User user=new User(uName,uEmail,mUser.getUid(),uMob,uDob,"default");
                    FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SaveUserDataActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Favourites favourites=new Favourites();
                            FirebaseFirestore.getInstance().collection("Favourites").document(mUser.getUid()).set(favourites).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Favourites added","Succesfully");
                                }
                            });
                            startActivity(new Intent(SaveUserDataActivity.this, MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SaveUserDataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }
        });

    }

    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }
}
