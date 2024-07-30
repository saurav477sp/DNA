package com.example.demo_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class login_page extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText editEmail, editPassword;
    private DatabaseReference mDatabase;
    private String selectedRole;
    private boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        Button btnLogin = findViewById(R.id.btn_login);
        LinearLayout login_with_google = findViewById(R.id.login_with_google);
        TextView create_new_account = findViewById(R.id.create_new_account);
        ImageView passwordVisibilityToggle = findViewById(R.id.password_visibility_toggle);
        TextView forgotPasswordTextView = findViewById(R.id.forgot_password);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        passwordVisibilityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });


        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        login_with_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        create_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getRole = getIntent();
                selectedRole = getRole.getStringExtra("selectedRole");

                Intent intent = new Intent(login_page.this, sign_up_page.class);
                intent.putExtra("selectedRole",selectedRole);
                startActivity(intent);

                editEmail.setText("");
                editPassword.setText("");
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login_page.this, "Please enter email and password",
                            Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(login_page.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(login_page.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(login_page.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Exception exception = task.getException();
                                        if (exception != null) {
                                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                                // User not found
                                                Toast.makeText(login_page.this, "No account found with this email. Please sign up.", Toast.LENGTH_SHORT).show();
                                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                                // Invalid password
                                                Toast.makeText(login_page.this, "Invalid password. Please try again.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Generic error
                                                Toast.makeText(login_page.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        editEmail.setText("");
                                        editPassword.setText("");
                                    }
                                }
                            });
                }
            }
        });

    }

    private void resetPassword(){
        String email = editEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(login_page.this, "Please enter your email", Toast.LENGTH_SHORT).show();
        }else {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Password reset email sent successfully
                                Toast.makeText(login_page.this, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Failed to send password reset email
                                Toast.makeText(login_page.this, "Failed to send password reset email. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    // Method to toggle password visibility
    private void togglePasswordVisibility() {
        if (!isPasswordVisible) {
            // Show password
            editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = true;
        } else {
            // Hide password
            editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = false;
        }
        // Move cursor to the end of the text
        editPassword.setSelection(editPassword.getText().length());
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
                        Intent intent = new Intent(login_page.this, mobile_number.class);
                        intent.putExtra("selectedRole",selectedRole);
                        startActivity(intent);

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
}

