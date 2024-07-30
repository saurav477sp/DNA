package com.example.demo_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class doctor_details extends AppCompatActivity {
    private Spinner speciality,paymentType;
    private EditText editTextName,editTextExperience,editTextHospitalName,editHospitalTextAddress,editTextRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        editTextName = findViewById(R.id.editTextName);
        speciality = findViewById(R.id.speciality);
        editTextExperience = findViewById(R.id.editTextExperience);
        editTextHospitalName = findViewById(R.id.editTextHospitalName);
        editHospitalTextAddress = findViewById(R.id.editHospitalTextAddress);
        editTextRating = findViewById(R.id.editTextRating);
        paymentType = findViewById(R.id.paymentType);
        Button buttonSave = findViewById(R.id.buttonSave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDoctorProfile();
            }
        });

    }

    private void saveDoctorProfile() {
        // Get values from EditText fields
        String name = editTextName.getText().toString();
        String specialty = speciality.getSelectedItem().toString();
        String experience = editTextExperience.getText().toString();
        String hospital_name = editTextHospitalName.getText().toString();
        String hospital_address = editHospitalTextAddress.getText().toString();
        String rating = editTextRating.getText().toString();
        String payment = paymentType.getSelectedItem().toString();

        if (name.isEmpty() || specialty.isEmpty() || experience.isEmpty() || hospital_name.isEmpty() || hospital_address.isEmpty() || rating.isEmpty() || payment.isEmpty()) {
            Toast.makeText(doctor_details.this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> doctorProfile = new HashMap<>();
        doctorProfile.put("name", name);
        doctorProfile.put("specialty", specialty);
        doctorProfile.put("experience", experience);
        doctorProfile.put("hospitalName", hospital_name);
        doctorProfile.put("address", hospital_address);
        doctorProfile.put("rating", rating);
        doctorProfile.put("payment", payment);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("doctors");


        if (user != null) {
            String userId = user.getUid();
            mDatabase.child(userId).child("profile").updateChildren(doctorProfile).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        storeDataAndRedirect();
                        Intent intent = new Intent(doctor_details.this, login_page.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(doctor_details.this, "Failed to store data", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if (account != null) {
            String googleUserId = account.getId();
            assert googleUserId != null;
            mDatabase.child(googleUserId).child("profile").updateChildren(doctorProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        storeDataAndRedirect();
                        Intent intent = new Intent(doctor_details.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(doctor_details.this, "Failed to store data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void storeDataAndRedirect(){
            editTextName.setText("");
            editTextHospitalName.setText("");
            editTextExperience.setText("");
            editHospitalTextAddress.setText("");
            editTextRating.setText("");
    }
}