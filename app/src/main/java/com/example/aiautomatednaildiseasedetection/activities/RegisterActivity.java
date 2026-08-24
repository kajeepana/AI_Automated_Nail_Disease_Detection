package com.example.aiautomatednaildiseasedetection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.User;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etProfessionalId;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private Button btnCreateAccount;
    private Button txtLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // =========================
        // RETROFIT
        // =========================

        apiService =
                RetrofitClient
                        .getClient()
                        .create(ApiService.class);


        // =========================
        // XML VIEWS
        // =========================

        etFullName =
                findViewById(R.id.etFullName);

        etProfessionalId =
                findViewById(R.id.etProfessionalId);

        etEmail =
                findViewById(R.id.etEmail);

        etPassword =
                findViewById(R.id.etPassword);

        etConfirmPassword =
                findViewById(R.id.etConfirmPassword);

        btnCreateAccount =
                findViewById(R.id.btnCreateAccount);

        txtLogin =
                findViewById(R.id.txtLogin);


        // =========================
        // CREATE ACCOUNT
        // =========================

        btnCreateAccount.setOnClickListener(view -> {

            String fullName =
                    etFullName
                            .getText()
                            .toString()
                            .trim();

            String professionalId =
                    etProfessionalId
                            .getText()
                            .toString()
                            .trim();

            String email =
                    etEmail
                            .getText()
                            .toString()
                            .trim();

            String password =
                    etPassword
                            .getText()
                            .toString()
                            .trim();

            String confirmPassword =
                    etConfirmPassword
                            .getText()
                            .toString()
                            .trim();


            // =========================
            // VALIDATION
            // =========================

            if (TextUtils.isEmpty(fullName)) {

                etFullName.setError(
                        "Enter your full name"
                );

                etFullName.requestFocus();

                return;
            }


            if (TextUtils.isEmpty(professionalId)) {

                etProfessionalId.setError(
                        "Enter your Professional ID"
                );

                etProfessionalId.requestFocus();

                return;
            }


            if (TextUtils.isEmpty(email)) {

                etEmail.setError(
                        "Enter your email"
                );

                etEmail.requestFocus();

                return;
            }


            if (TextUtils.isEmpty(password)) {

                etPassword.setError(
                        "Enter your password"
                );

                etPassword.requestFocus();

                return;
            }


            if (password.length() < 8) {

                etPassword.setError(
                        "Password must be at least 8 characters"
                );

                etPassword.requestFocus();

                return;
            }


            if (!password.matches(".*[A-Za-z].*")) {

                etPassword.setError(
                        "Password must contain at least one letter"
                );

                etPassword.requestFocus();

                return;
            }


            if (!password.matches(".*[0-9].*")) {

                etPassword.setError(
                        "Password must contain at least one number"
                );

                etPassword.requestFocus();

                return;
            }


            if (TextUtils.isEmpty(confirmPassword)) {

                etConfirmPassword.setError(
                        "Confirm your password"
                );

                etConfirmPassword.requestFocus();

                return;
            }


            if (!password.equals(confirmPassword)) {

                etConfirmPassword.setError(
                        "Passwords do not match"
                );

                etConfirmPassword.requestFocus();

                return;
            }


            // =========================
            // CREATE USER OBJECT
            // =========================

            User user = new User();


            String[] names =
                    fullName.split(" ", 2);


            user.setFirstName(names[0]);


            if (names.length > 1) {

                user.setLastName(names[1]);

            } else {

                user.setLastName("User");
            }


            user.setProfessionalId(
                    professionalId
            );

            user.setEmail(email);

            user.setPassword(password);

            user.setRole("USER");


            // =========================
            // REGISTER USER
            // =========================

            btnCreateAccount.setEnabled(false);


            apiService
                    .registerUser(user)
                    .enqueue(new Callback<User>() {

                        @Override
                        public void onResponse(
                                Call<User> call,
                                Response<User> response) {

                            btnCreateAccount.setEnabled(true);


                            if (response.isSuccessful()
                                    && response.body() != null) {


                                // =========================
                                // REGISTRATION SUCCESS
                                // =========================

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Registration Successful! OTP sent to your email.",
                                        Toast.LENGTH_LONG
                                ).show();


                                // =========================
                                // GO TO VERIFY OTP
                                // =========================

                                Intent intent =
                                        new Intent(
                                                RegisterActivity.this,
                                                VerifyOtpActivity.class
                                        );


                                // Send email

                                intent.putExtra(
                                        "email",
                                        email
                                );


                                // IMPORTANT
                                // This tells VerifyOtpActivity
                                // that OTP is for registration

                                intent.putExtra(
                                        "fromRegister",
                                        true
                                );


                                startActivity(intent);

                                finish();


                            } else {


                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Error Code : "
                                                + response.code(),
                                        Toast.LENGTH_LONG
                                ).show();


                                try {

                                    if (response.errorBody()
                                            != null) {

                                        Toast.makeText(
                                                RegisterActivity.this,
                                                response.errorBody()
                                                        .string(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                        }


                        @Override
                        public void onFailure(
                                Call<User> call,
                                Throwable t) {

                            btnCreateAccount.setEnabled(true);


                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Error : "
                                            + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });

        });


        // =========================
        // LOGIN BUTTON
        // =========================

        txtLogin.setOnClickListener(view -> {

            Intent intent =
                    new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

            startActivity(intent);

            finish();
        });
    }
}