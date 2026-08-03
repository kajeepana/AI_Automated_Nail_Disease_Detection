package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnCreateAccount, txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        txtLogin = findViewById(R.id.txtLogin);

        // Create Account Button
        btnCreateAccount.setOnClickListener(view -> {

            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(fullName)) {
                etFullName.setError("Enter your full name");
                etFullName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter your email");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Enter your password");
                etPassword.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                etConfirmPassword.setError("Confirm your password");
                etConfirmPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                etConfirmPassword.setError("Passwords do not match");
                etConfirmPassword.requestFocus();
                return;
            }

            // Backend not connected yet
            Toast.makeText(RegisterActivity.this,
                    "Validation Successful! Backend connection will be added later.",
                    Toast.LENGTH_LONG).show();

            // Go to Login Screen
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        });

        // Login Button
        txtLogin.setOnClickListener(view -> {

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        });
    }
}