package com.example.aiautomatednaildiseasedetection.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aiautomatednaildiseasedetection.R;

public class LeaveActivity extends AppCompatActivity {

    Button btnLogout, btnContinue;
    TextView txtCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showLogoutDialog();
    }

    private void showLogoutDialog() {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(false);

        btnContinue = dialog.findViewById(R.id.btnContinue);
        btnLogout = dialog.findViewById(R.id.btnLogout);
        txtCancel = dialog.findViewById(R.id.txtCancel);

        // Continue Session
        btnContinue.setOnClickListener(v -> dialog.dismiss());

        // Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(LeaveActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Cancel
        txtCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}