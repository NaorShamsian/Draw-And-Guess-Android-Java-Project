package com.example.draw_and_guess_naor_shamsian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends Activity {
    Button registerBtn;
    EditText nameEditText, emailEditText, passwordEditText, validatePasswordEditText;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        registerBtn = (Button) findViewById(R.id.registerNowBtn);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.password);
        validatePasswordEditText = (EditText) findViewById(R.id.validatePassword);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // vaild email

            @Override
            public void onClick(View view) {
                final String name = nameEditText.getText().toString().trim();

                if (name.isEmpty() || name.length() > 20) {
                    nameEditText.setError("Name should be less than 10 characters");
                    nameEditText.requestFocus();
                    return;
                }
                if ((!(emailEditText.getText().toString().trim().matches(emailPattern)) || emailEditText.getText().toString().trim().length() == 0)) {
                    emailEditText.setError("Please enter vaild email address");
                    emailEditText.requestFocus();
                    return;
                }
                if ((!passwordEditText.getText().toString().trim().equals(validatePasswordEditText.getText().toString().trim())))
                {
                    validatePasswordEditText.setError("Password and Password Authentication doesn't match");
                    validatePasswordEditText.requestFocus();
                    return;
                }
                if (passwordEditText.getText().toString().trim().length() < 6) {
                    passwordEditText.setError("Password length can't be less 6 characters");
                    passwordEditText.requestFocus();
                    return;
                }
                // Register user
                firebaseAuth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference users = mDatabase.getReference("Users");
                            users.child(firebaseAuth.getUid()).setValue(name);
                            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, GameActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });


    }


}
