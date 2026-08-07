package com.example.aiautomatednaildiseasedetection.activities;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.User;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        apiService = RetrofitClient.getClient().create(ApiService.class);

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


            User user = new User();


            String[] names = fullName.split(" ", 2);

            user.setFirstName(names[0]);

            if (names.length > 1) {
                user.setLastName(names[1]);
            } else {
                user.setLastName("User");
            }

            user.setEmail(email);
            user.setPassword(password);
            user.setRole("USER");

            apiService.registerUser(user).enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    if (response.isSuccessful()) {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration Successful!",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                RegisterActivity.this,
                                "Error Code : " + response.code(),
                                Toast.LENGTH_LONG
                        ).show();

                        try {
                            if (response.errorBody() != null) {
                                Toast.makeText(
                                        RegisterActivity.this,
                                        response.errorBody().string(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Error : " + t.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                }
            });

        });

        // Login Button
        txtLogin.setOnClickListener(view -> {

            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        });
    }
}