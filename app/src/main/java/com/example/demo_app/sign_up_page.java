package com.example.demo_app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class sign_up_page extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText edit_username, emailEditText, edit_password;
    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        Intent roleOfUser = getIntent();
        selectedRole = roleOfUser.getStringExtra("selectedRole");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edit_username = findViewById(R.id.edit_username);
        emailEditText = findViewById(R.id.emailEditText);
        edit_password = findViewById(R.id.edit_password);
        Button sign_up_button = findViewById(R.id.sign_up_button);
        LinearLayout login_with_google = findViewById(R.id.login_with_google);

        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        login_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp(){
        // Get user input
        String username = edit_username.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        if (validateFields(username, email, password)) {
            checkEmailExistence(email);
        }
    }

    private boolean validateFields(@NonNull String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(sign_up_page.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()-+=])(?=\\S+$).{8,}$";
        boolean isValidPassword = password.matches(passwordPattern);
        if (!isValidPassword) {
            Toast.makeText(sign_up_page.this, "use strong password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkEmailExistence(String email) {
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null && !result.getSignInMethods().isEmpty()) {
                        Toast.makeText(sign_up_page.this, "You already have an account. Please log in.", Toast.LENGTH_LONG).show();
                    } else {
                        createUser(email, edit_password.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(sign_up_page.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(sign_up_page.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        User newUser = new User(edit_username.getText().toString().trim(),email);
                        if (selectedRole.equals("I am a Doctor")){
                            mDatabase.child("doctors").child(userId).setValue(newUser);
                        }else {
                            mDatabase.child("users").child(userId).setValue(newUser);
                        }
                    }
                    nextPage();

                } else {
                    Toast.makeText(sign_up_page.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        String userId = account.getId();
                        String username = account.getDisplayName();
                        String email = account.getEmail();

                        Intent getRole = getIntent();
                        selectedRole = getRole.getStringExtra("selectedRole");

                        User newUser = new User(username, email);
                        assert userId != null;
                        if (Objects.equals(selectedRole, "I am a Doctor")){
                            mDatabase.child("doctors").child(userId).setValue(newUser);

                        }else {
                            mDatabase.child("users").child(userId).setValue(newUser);
                        }
//                        Intent intent = new Intent(sign_up_page.this, MainActivity.class);
//                        startActivity(intent);
                        nextPage();

                    }
                } catch (ApiException e) {
                    Toast.makeText(this, "add data failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle unsuccessful task
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void nextPage(){
        Intent intent = new Intent(sign_up_page.this, mobile_number.class);
        intent.putExtra("selectedRole",selectedRole);
        startActivity(intent);
        finish();
    }


}