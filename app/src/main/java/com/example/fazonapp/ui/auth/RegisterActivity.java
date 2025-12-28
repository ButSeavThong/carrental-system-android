package com.example.fazonapp.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.fazonapp.R;
import com.example.fazonapp.dto.RegisterDTO;
import com.example.fazonapp.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPhone, etPassword;
    private RadioGroup rgNationality;
    private RadioButton rbCambodian, rbForeigner;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        rgNationality = findViewById(R.id.rgNationality);
        rbCambodian = findViewById(R.id.rbCambodian);
        rbForeigner = findViewById(R.id.rbForeigner);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupObservers() {
        viewModel.getAuthResult().observe(this, result -> {
            if (result.isSuccess()) {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                // Go to login screen
                finish();
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnRegister.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
            }
        });
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());

        tvLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        String nationality = rbCambodian.isChecked() ?
                Constants.NATIONALITY_CAMBODIAN : Constants.NATIONALITY_FOREIGNER;

        RegisterDTO registerDTO = new RegisterDTO(name, email, password, phone, nationality);

        viewModel.register(registerDTO);
    }
}
