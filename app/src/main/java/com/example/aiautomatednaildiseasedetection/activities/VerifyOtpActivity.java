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

    // This tells us whether OTP came from Registration
    private boolean fromRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verify_otp);


        // =========================
        // INITIALIZE API
        // =========================

        apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        // =========================
        // FIND XML VIEWS
        // =========================

        etOtp =
                findViewById(R.id.etOtp);

        btnVerifyOtp =
                findViewById(R.id.btnVerifyOtp);

        txtBackToForgot =
                findViewById(R.id.txtBackToForgot);


        // =========================
        // GET EMAIL
        // =========================

        email =
                getIntent()
                        .getStringExtra("email");


        // =========================
        // CHECK OTP SOURCE
        // =========================

        fromRegister =
                getIntent()
                        .getBooleanExtra(
                                "fromRegister",
                                false
                        );


        // =========================
        // CHECK EMAIL
        // =========================

        if (email == null
                || email.trim().isEmpty()) {

            Toast.makeText(
                    VerifyOtpActivity.this,
                    "Email not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =========================
        // VERIFY OTP BUTTON
        // =========================

        btnVerifyOtp.setOnClickListener(v -> {


            String otp =
                    etOtp
                            .getText()
                            .toString()
                            .trim();


            // =========================
            // OTP EMPTY
            // =========================

            if (otp.isEmpty()) {

                etOtp.setError(
                        "Enter OTP"
                );

                etOtp.requestFocus();

                return;
            }


            // =========================
            // OTP LENGTH
            // =========================

            if (otp.length() != 6) {

                etOtp.setError(
                        "Enter 6-digit OTP"
                );

                etOtp.requestFocus();

                return;
            }


            // =========================
            // CREATE REQUEST
            // =========================

            VerifyOtpRequest request =
                    new VerifyOtpRequest(
                            email,
                            otp
                    );


            // Disable button
            // while API is running

            btnVerifyOtp.setEnabled(false);


            // =========================
            // CALL BACKEND
            // =========================

            apiService
                    .verifyOtp(request)
                    .enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(
                                Call<String> call,
                                Response<String> response) {


                            // Enable button again

                            btnVerifyOtp
                                    .setEnabled(true);


                            // =========================
                            // OTP SUCCESS
                            // =========================

                            if (response.isSuccessful()) {


                                Toast.makeText(
                                        VerifyOtpActivity.this,
                                        "OTP verified successfully",
                                        Toast.LENGTH_LONG
                                ).show();


                                // ==================================
                                // REGISTRATION OTP
                                // ==================================

                                if (fromRegister) {


                                    Intent intent =
                                            new Intent(
                                                    VerifyOtpActivity.this,
                                                    LoginActivity.class
                                            );


                                    // Send email to LoginActivity

                                    intent.putExtra(
                                            "email",
                                            email
                                    );


                                    startActivity(intent);


                                }

                                // ==================================
                                // FORGOT PASSWORD OTP
                                // ==================================

                                else {


                                    Intent intent =
                                            new Intent(
                                                    VerifyOtpActivity.this,
                                                    ResetPasswordActivity.class
                                            );


                                    // Send email to Reset Password

                                    intent.putExtra(
                                            "email",
                                            email
                                    );


                                    startActivity(intent);
                                }


                                finish();


                            }

                            // =========================
                            // OTP FAILED
                            // =========================

                            else {


                                String errorMessage =
                                        "Invalid or expired OTP";


                                if (response.errorBody()
                                        != null) {

                                    try {

                                        String serverMessage =
                                                response
                                                        .errorBody()
                                                        .string();


                                        if (serverMessage != null
                                                && !serverMessage
                                                .trim()
                                                .isEmpty()) {

                                            errorMessage =
                                                    serverMessage;
                                        }

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


                        // =========================
                        // CONNECTION ERROR
                        // =========================

                        @Override
                        public void onFailure(
                                Call<String> call,
                                Throwable t) {


                            btnVerifyOtp
                                    .setEnabled(true);


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
        // BACK BUTTON
        // =========================

        txtBackToForgot.setOnClickListener(v -> {

            finish();

        });
    }
}