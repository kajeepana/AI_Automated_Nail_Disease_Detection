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
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.LoginRequest;
import com.example.aiautomatednaildiseasedetection.model.User;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private TextView txtForgot;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Initialize Retrofit API
        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Find Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        txtForgot = findViewById(R.id.txtForgot);

        // =========================
        // LOGIN BUTTON
        // =========================

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Email empty validation
            if (TextUtils.isEmpty(email)) {

                etEmail.setError("Enter Email");
                etEmail.requestFocus();
                return;
            }

            // Email format validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                etEmail.setError("Enter Valid Email");
                etEmail.requestFocus();
                return;
            }

            // Password empty validation
            if (TextUtils.isEmpty(password)) {

                etPassword.setError("Enter Password");
                etPassword.requestFocus();
                return;
            }

            // Create LoginRequest
            LoginRequest loginRequest =
                    new LoginRequest(email, password);

            // Send Login Request
            apiService.loginUser(loginRequest)
                    .enqueue(new Callback<User>() {

                        @Override
                        public void onResponse(
                                Call<User> call,
                                Response<User> response) {

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                User user = response.body();

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Login Successful!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Go to Profile Activity
                                Intent intent = new Intent(
                                        LoginActivity.this,
                                        ProfileActivity.class
                                );

                                // Send user details
                                intent.putExtra(
                                        "email",
                                        user.getEmail()
                                );

                                intent.putExtra(
                                        "firstName",
                                        user.getFirstName()
                                );

                                intent.putExtra(
                                        "lastName",
                                        user.getLastName()
                                );

                                startActivity(intent);

                                finish();

                            } else {

                                Toast.makeText(
                                        LoginActivity.this,
                                        "Invalid Email or Password",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<User> call,
                                Throwable t) {

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Error: " + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // =========================
        // REGISTER BUTTON
        // =========================

        btnRegister.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // FORGOT PASSWORD
        // =========================

        txtForgot.setOnClickListener(v -> {

            Intent intent = new Intent(
                    LoginActivity.this,
                    ForgotPasswordActivity.class
            );

            startActivity(intent);
        });
    }
}