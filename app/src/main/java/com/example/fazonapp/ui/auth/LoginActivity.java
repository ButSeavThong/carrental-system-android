package com.example.fazonapp.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.fazonapp.R;
import com.example.fazonapp.ui.admin.AdminDashboardActivity;
import com.example.fazonapp.ui.customer.HomeActivity;
import com.example.fazonapp.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Check if user is already logged in
        if (viewModel.isUserLoggedIn()) {
            // Will redirect after loading user data
            viewModel.getCurrentUser().observe(this, user -> {
                if (user != null) {
                    redirectBasedOnRole(user.getRole());
                }
            });
            return;
        }

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupObservers() {
        // Observe authentication result
        viewModel.getAuthResult().observe(this, result -> {
            if (result.isSuccess()) {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                redirectBasedOnRole(result.getUserRole());
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnLogin.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
            }
        });
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        viewModel.login(email, password);
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;

        if (Constants.ROLE_ADMIN.equals(role)) {
            intent = new Intent(this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}