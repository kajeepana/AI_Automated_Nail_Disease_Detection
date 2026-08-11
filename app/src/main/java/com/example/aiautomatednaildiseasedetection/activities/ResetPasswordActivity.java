package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.dto.ResetPasswordRequest;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private Button btnResetPassword;
    private TextView txtBackToLogin;

    private ApiService apiService;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reset_password);

        // Initialize API
        apiService = RetrofitClient
                .getClient()
                .create(ApiService.class);

        // Find views
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

        // Get email from VerifyOtpActivity
        email = getIntent().getStringExtra("email");


        // =========================
        // RESET PASSWORD
        // =========================

        btnResetPassword.setOnClickListener(v -> {

            String newPassword =
                    etNewPassword.getText().toString().trim();

            String confirmPassword =
                    etConfirmPassword.getText().toString().trim();


            // Check new password
            if (TextUtils.isEmpty(newPassword)) {

                etNewPassword.setError(
                        "Enter new password"
                );

                etNewPassword.requestFocus();

                return;
            }


            // Check minimum password length
            if (newPassword.length() < 6) {

                etNewPassword.setError(
                        "Password must be at least 6 characters"
                );

                etNewPassword.requestFocus();

                return;
            }


            // Check confirm password
            if (TextUtils.isEmpty(confirmPassword)) {

                etConfirmPassword.setError(
                        "Confirm your password"
                );

                etConfirmPassword.requestFocus();

                return;
            }


            // Check passwords match
            if (!newPassword.equals(confirmPassword)) {

                etConfirmPassword.setError(
                        "Passwords do not match"
                );

                etConfirmPassword.requestFocus();

                return;
            }


            // Check email
            if (email == null || email.isEmpty()) {

                Toast.makeText(
                        ResetPasswordActivity.this,
                        "Email not found",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }


            // Create request
            ResetPasswordRequest request =
                    new ResetPasswordRequest(
                            email,
                            newPassword
                    );


            // Call backend
            apiService
                    .resetPassword(request)
                    .enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(
                                Call<String> call,
                                Response<String> response) {

                            if (response.isSuccessful()) {

                                Toast.makeText(
                                        ResetPasswordActivity.this,
                                        "Password reset successfully",
                                        Toast.LENGTH_LONG
                                ).show();


                                // Go back to Login
                                Intent intent =
                                        new Intent(
                                                ResetPasswordActivity.this,
                                                LoginActivity.class
                                        );

                                // Clear previous screens
                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                );

                                startActivity(intent);

                                finish();

                            } else {

                                String errorMessage =
                                        "Password reset failed";

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
                                        ResetPasswordActivity.this,
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
                                    ResetPasswordActivity.this,
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

        txtBackToLogin.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ResetPasswordActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finish();
        });
    }
}