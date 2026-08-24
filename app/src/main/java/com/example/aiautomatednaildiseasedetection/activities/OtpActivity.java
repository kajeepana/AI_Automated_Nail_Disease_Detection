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

public class OtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp;
    private TextView txtEmail;

    private ApiService apiService;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_otp);

        // =========================
        // CONNECT XML VIEWS
        // =========================

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        txtEmail = findViewById(R.id.txtEmail);


        // =========================
        // RETROFIT
        // =========================

        apiService =
                RetrofitClient.getClient().create(ApiService.class);


        // =========================
        // GET EMAIL
        // =========================

        email = getIntent().getStringExtra("email");

        if (email != null) {
            txtEmail.setText(email);
        }


        // =========================
        // VERIFY OTP BUTTON
        // =========================

        btnVerifyOtp.setOnClickListener(v -> {

            String otp =
                    etOtp.getText().toString().trim();


            // OTP EMPTY
            if (otp.isEmpty()) {

                etOtp.setError("Enter OTP");
                etOtp.requestFocus();
                return;
            }


            // OTP LENGTH
            if (otp.length() != 6) {

                etOtp.setError("Enter 6-digit OTP");
                etOtp.requestFocus();
                return;
            }


            // EMAIL CHECK
            if (email == null || email.isEmpty()) {

                Toast.makeText(
                        OtpActivity.this,
                        "Email not found",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }


            // VERIFY OTP
            verifyOtp(email, otp);
        });
    }


    // =========================
    // VERIFY OTP METHOD
    // =========================

    private void verifyOtp(String email, String otp) {

        // Create VerifyOtpRequest
        VerifyOtpRequest request =
                new VerifyOtpRequest(email, otp);


        // Send request to backend
        apiService.verifyOtp(request)
                .enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(
                            Call<String> call,
                            Response<String> response) {

                        if (response.isSuccessful()) {

                            Toast.makeText(
                                    OtpActivity.this,
                                    "Email Verified Successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();


                            // Go to Login Screen
                            Intent intent =
                                    new Intent(
                                            OtpActivity.this,
                                            LoginActivity.class
                                    );

                            startActivity(intent);

                            finish();

                        } else {

                            Toast.makeText(
                                    OtpActivity.this,
                                    "Invalid or Expired OTP",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }


                    @Override
                    public void onFailure(
                            Call<String> call,
                            Throwable t) {

                        Toast.makeText(
                                OtpActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}