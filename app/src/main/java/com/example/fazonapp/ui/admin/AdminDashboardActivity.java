package com.example.fazonapp.ui.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.example.fazonapp.R;
import com.example.fazonapp.data.repository.AuthRepository;
import com.example.fazonapp.ui.auth.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView cardManageCars, cardManageBookings, cardManageUsers, cardLogout;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        authRepository = new AuthRepository();

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cardManageCars = findViewById(R.id.cardManageCars);
        cardManageBookings = findViewById(R.id.cardManageBookings);
        cardManageUsers = findViewById(R.id.cardManageUsers);
        cardLogout = findViewById(R.id.cardLogout);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
        }
    }

    private void setupListeners() {
        cardManageCars.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageCarActivity.class);
            startActivity(intent);
        });

        cardManageBookings.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageBookingActivity.class);
            startActivity(intent);
        });

        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageUserActivity.class);
            startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        authRepository.logoutUser();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            Toast.makeText(this, "Dashboard refreshed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
