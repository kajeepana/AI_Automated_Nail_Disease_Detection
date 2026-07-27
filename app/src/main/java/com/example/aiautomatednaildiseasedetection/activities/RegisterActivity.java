package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnCreateAccount, txtLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Database
        databaseHelper = new DatabaseHelper(this);

        // Initialize Views
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

            // Check if email already exists
            if (databaseHelper.checkEmail(email)) {

                Toast.makeText(RegisterActivity.this,
                        "Email already exists!",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            // Register User
            boolean success = databaseHelper.registerUser(
                    fullName,
                    email,
                    password
            );

            if (success) {

                Toast.makeText(RegisterActivity.this,
                        "Registration Successful!",
                        Toast.LENGTH_SHORT).show();

                startActivity(new Intent(
                        RegisterActivity.this,
                        LoginActivity.class));

                finish();

            } else {

                Toast.makeText(RegisterActivity.this,
                        "Registration Failed!",
                        Toast.LENGTH_SHORT).show();
            }

        });

        // Login Button
        txtLogin.setOnClickListener(view -> {

            startActivity(new Intent(
                    RegisterActivity.this,
                    LoginActivity.class));

            finish();

        });

    }
}