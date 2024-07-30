package com.example.demo_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class doctor_or_patient extends AppCompatActivity {

    private Spinner roleSpinner;
    private FirebaseAuth mAuth;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(doctor_or_patient.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_or_patient);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            Intent intent = new Intent(doctor_or_patient.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        roleSpinner = findViewById(R.id.roleSpinner);
        Button nextButton = findViewById(R.id.btn_login);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedRole = roleSpinner.getSelectedItem().toString();

                    Intent intent = new Intent(doctor_or_patient.this, login_page.class);
                    intent.putExtra("selectedRole",selectedRole);
                    startActivity(intent);

            }
        });
    }
}
