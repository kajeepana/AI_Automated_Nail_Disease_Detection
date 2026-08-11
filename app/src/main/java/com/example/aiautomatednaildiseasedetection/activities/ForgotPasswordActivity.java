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
import com.example.aiautomatednaildiseasedetection.dto.ForgotPasswordRequest;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendOtp;
    private TextView txtBackLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);

        // Initialize API
        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        // Find views
        etEmail = findViewById(R.id.etResetEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        txtBackLogin = findViewById(R.id.txtBackToLogin);


        // =========================
        // SEND OTP
        // =========================

        btnSendOtp.setOnClickListener(v -> {

            String email =
                    etEmail.getText().toString().trim();

            // Check empty email
            if (TextUtils.isEmpty(email)) {

                etEmail.setError("Enter your email");
                etEmail.requestFocus();

                return;
            }

            // Check valid email
            if (!Patterns.EMAIL_ADDRESS
                    .matcher(email)
                    .matches()) {

                etEmail.setError("Enter a valid email");
                etEmail.requestFocus();

                return;
            }


            // Create request
            ForgotPasswordRequest request =
                    new ForgotPasswordRequest(email);


            // Call backend
            apiService
                    .forgotPassword(request)
                    .enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(
                                Call<String> call,
                                Response<String> response) {

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        ForgotPasswordActivity.this,
                                        "OTP sent to your email",
                                        Toast.LENGTH_LONG
                                ).show();


                                // Open OTP screen
                                Intent intent =
                                        new Intent(
                                                ForgotPasswordActivity.this,
                                                VerifyOtpActivity.class
                                        );

                                intent.putExtra(
                                        "email",
                                        email
                                );

                                startActivity(intent);

                            } else {

                                String errorMessage =
                                        "Failed to send OTP";

                                if (response.errorBody() != null) {

                                    try {

                                        errorMessage =
                                                response
                                                        .errorBody()
                                                        .string();

                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }
                                }


                                Toast.makeText(
                                        ForgotPasswordActivity.this,
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }


                        @Override
                        public void onFailure(
                                Call<String> call,
                                Throwable t) {

                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Connection Error: "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // =========================
        // BACK TO LOGIN
        // =========================

        txtBackLogin.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ForgotPasswordActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finish();
        });
    }
}