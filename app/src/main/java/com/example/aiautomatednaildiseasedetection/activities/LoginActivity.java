package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView txtForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        txtForgot = findViewById(R.id.txtForgot);

        // Login Button
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Email");
                etEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter Valid Email");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter Password");
                etPassword.requestFocus();
                return;
            }

            // TODO: Retrofit Login API will be added later

            Toast.makeText(
                    LoginActivity.this,
                    "Login Successful!",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    LoginActivity.this,
                    ProfileActivity.class
            );

            intent.putExtra("email", email);

            startActivity(intent);
            finish();

        });

        // Register Button
        btnRegister.setOnClickListener(v -> {

            Toast.makeText(
                    LoginActivity.this,
                    "Register Screen Coming Soon",
                    Toast.LENGTH_SHORT
            ).show();

        });

        // Forgot Password
        txtForgot.setOnClickListener(v -> {

            Toast.makeText(
                    LoginActivity.this,
                    "Forgot Password feature coming soon!",
                    Toast.LENGTH_SHORT
            ).show();

        });

    }
}