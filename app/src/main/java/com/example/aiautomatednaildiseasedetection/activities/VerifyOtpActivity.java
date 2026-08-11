package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.dto.VerifyOtpRequest;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp;
    private TextView txtBackToForgot;

    private ApiService apiService;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_otp);

        // Initialize API
        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        // Find views
        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        txtBackToForgot = findViewById(R.id.txtBackToForgot);

        // Get email from ForgotPasswordActivity
        email = getIntent().getStringExtra("email");


        // =========================
        // VERIFY OTP
        // =========================

        btnVerifyOtp.setOnClickListener(v -> {

            String otp = etOtp.getText()
                    .toString()
                    .trim();

            if (otp.isEmpty()) {

                etOtp.setError("Enter OTP");
                etOtp.requestFocus();

                return;
            }

            if (otp.length() != 6) {

                etOtp.setError("Enter 6-digit OTP");
                etOtp.requestFocus();

                return;
            }

            if (email == null || email.isEmpty()) {

                Toast.makeText(
                        VerifyOtpActivity.this,
                        "Email not found",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }


            // Create request
            VerifyOtpRequest request =
                    new VerifyOtpRequest(
                            email,
                            otp
                    );


            // Call backend
            apiService
                    .verifyOtp(request)
                    .enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(
                                Call<String> call,
                                Response<String> response) {

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        VerifyOtpActivity.this,
                                        "OTP verified successfully",
                                        Toast.LENGTH_LONG
                                ).show();


                                // Go to Reset Password screen
                                Intent intent =
                                        new Intent(
                                                VerifyOtpActivity.this,
                                                ResetPasswordActivity.class
                                        );

                                intent.putExtra(
                                        "email",
                                        email
                                );

                                startActivity(intent);

                                finish();

                            } else {

                                String errorMessage =
                                        "Invalid or expired OTP";

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
                                        VerifyOtpActivity.this,
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
                                    VerifyOtpActivity.this,
                                    "Connection Error: "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });


        // =========================
        // BACK
        // =========================

        txtBackToForgot.setOnClickListener(v -> {

            finish();
        });
    }
}