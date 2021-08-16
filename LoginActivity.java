package com.example.draw_and_guess_naor_shamsian;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class LoginActivity extends Activity {

    private EditText EditTextEmail, EditTextPassword;
    String email, password;
    FirebaseAuth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = FirebaseAuth.getInstance();
        EditTextEmail = (EditText) findViewById(R.id.emailLogin);
        EditTextPassword = (EditText) findViewById(R.id.passwordLogin);
        findViewById(R.id.signIn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                email = EditTextEmail.getText().toString().trim();
                password = EditTextPassword.getText().toString().trim();
                if(email.length()==0 || password.length() == 0)
                {
                    EditTextEmail.setError("Enter email please");
                    EditTextPassword.setError("Enter password please");
                    return;
                }
                user.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6 || !Validate()) {
                                        EditTextPassword.setError("Password length can't be less 6 characters");
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Email or password is incorrect", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
        findViewById(R.id.btn_reset_password).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,PasswordRestActivity.class);
                startActivity(intent);
            }
        });
    }


    public boolean Validate() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // vaild email
        if ((!(email.matches(emailPattern)) || email.length() == 0)) {
            EditTextEmail.setError("Please enter vaild email address");
            EditTextEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (user.getCurrentUser() != null) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


    }
}




