package com.example.aiautomatednaildiseasedetection.activities;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;
import com.example.aiautomatednaildiseasedetection.api.ApiService;
import com.example.aiautomatednaildiseasedetection.model.User;
import com.example.aiautomatednaildiseasedetection.network.RetrofitClient;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etEmail;
    private EditText etDOB;
    private EditText etAge;

    private ImageView imgProfile;

    private ImageButton btnBack;

    private Button btnChoosePhoto;
    private Button btnSave;
    private Button btnSkip;
    private Button btnLogout;
    private Button btnHistory;

    private String loggedInEmail;
    private ApiService apiService;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        // Logged in email
        loggedInEmail = getIntent().getStringExtra("email");

        // Views
        imgProfile = findViewById(R.id.imgProfile);

        btnBack = findViewById(R.id.btnBack);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etDOB = findViewById(R.id.etDOB);
        etAge = findViewById(R.id.etAge);

        btnSave = findViewById(R.id.btnSave);
        btnSkip = findViewById(R.id.btnSkip);
        btnLogout = findViewById(R.id.btnLogout);
        btnHistory = findViewById(R.id.btnHistory);


        if (loggedInEmail != null) {
            etEmail.setText(loggedInEmail);
        }

        // Load saved profile
        if (loggedInEmail != null) {
        }

        // Gallery Image Picker
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null) {

                        Uri imageUri = result.getData().getData();

                        imgProfile.setImageURI(imageUri);

                    }

                });

        btnChoosePhoto.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            galleryLauncher.launch(intent);

        });



        btnBack.setOnClickListener(v -> finish());



        etDOB.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            ProfileActivity.this,

                            (view, selectedYear, selectedMonth, selectedDay) -> {

                                String dob = selectedDay + "/"
                                        + (selectedMonth + 1) + "/"
                                        + selectedYear;

                                etDOB.setText(dob);

                                int currentYear =
                                        Calendar.getInstance().get(Calendar.YEAR);

                                int age = currentYear - selectedYear;

                                etAge.setText(String.valueOf(age));

                            },

                            year,
                            month,
                            day);

            datePickerDialog.show();

        });


        btnSave.setOnClickListener(v -> {

            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String dob = etDOB.getText().toString().trim();
            String age = etAge.getText().toString().trim();


            if (TextUtils.isEmpty(firstName)) {
                etFirstName.setError("Enter First Name");
                etFirstName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(lastName)) {
                etLastName.setError("Enter Last Name");
                etLastName.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter Email");
                etEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter Valid Email");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(dob)) {
                etDOB.setError("Select Date of Birth");
                etDOB.requestFocus();
                return;
            }

            // Save Profile to SQLite
            boolean success = true;
            if (success) {

                Toast.makeText(
                        ProfileActivity.this,
                        "Profile Saved Successfully!",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(
                        ProfileActivity.this,
                        UploadImageActivity.class
                );

                // Pass email to next screen
                intent.putExtra("email", email);

                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        ProfileActivity.this,
                        "Failed to Save Profile",
                        Toast.LENGTH_SHORT
                ).show();

            }

        });



        btnSkip.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    UploadImageActivity.class
            );

            intent.putExtra("email", loggedInEmail);

            startActivity(intent);

            finish();

        });
        btnLogout.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    LeaveActivity.class
            );

            startActivity(intent);

        });
        btnHistory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ProfileActivity.this,
                    AnalysisHistoryActivity.class
            );

            intent.putExtra("email", loggedInEmail);

            startActivity(intent);
        });

    }
}