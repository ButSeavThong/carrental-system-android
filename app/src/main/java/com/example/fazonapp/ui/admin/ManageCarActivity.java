package com.example.fazonapp.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fazonapp.R;
import com.example.fazonapp.adapter.AdminCarAdapter;
import com.example.fazonapp.dto.CarDTO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ManageCarActivity extends AppCompatActivity implements AdminCarAdapter.OnCarActionListener {

    private Toolbar toolbar;
    private RecyclerView rvCars;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCar;

    private AdminCarAdapter carAdapter;
    private AdminCarViewModel viewModel;

    // For image selection
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private boolean isEditMode = false;
    private CarDTO currentCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_car);

        viewModel = new ViewModelProvider(this).get(AdminCarViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupObservers();

        // Load cars
        viewModel.loadAllCars();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvCars = findViewById(R.id.rvCars);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);
        fabAddCar = findViewById(R.id.fabAddCar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Cars");
        }
    }

    private void setupRecyclerView() {
        carAdapter = new AdminCarAdapter(this);
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        rvCars.setAdapter(carAdapter);
    }

    private void setupObservers() {
        viewModel.getCarsLiveData().observe(this, cars -> {
            if (cars != null && !cars.isEmpty()) {
                carAdapter.setCars(cars);
                rvCars.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                rvCars.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(CarDTO car) {
        currentCar = car;
        isEditMode = true;
        showAddEditCarDialog(car);
    }

    @Override
    public void onDeleteClick(CarDTO car) {
        showDeleteConfirmationDialog(car);
    }

    private void showAddEditCarDialog(@Nullable CarDTO car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_car, null);
        builder.setView(dialogView);

        // Initialize dialog views
        EditText etCarName = dialogView.findViewById(R.id.etCarName);
        EditText etCarBrand = dialogView.findViewById(R.id.etCarBrand);
        Spinner spinnerCarType = dialogView.findViewById(R.id.spinnerCarType);
        EditText etCarPrice = dialogView.findViewById(R.id.etCarPrice);
        SwitchMaterial switchAvailability = dialogView.findViewById(R.id.switchAvailability);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        ImageView ivCarPreview = dialogView.findViewById(R.id.ivCarPreview);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        ProgressBar progressBarDialog = dialogView.findViewById(R.id.progressBar);

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.car_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCarType.setAdapter(adapter);

        // If editing, populate fields
        if (car != null) {
            dialogView.findViewById(R.id.tvDialogTitle).setVisibility(View.GONE);
            etCarName.setText(car.getName());
            etCarBrand.setText(car.getBrand());

            // Set spinner to car type
            for (int i = 0; i < spinnerCarType.getCount(); i++) {
                if (spinnerCarType.getItemAtPosition(i).toString().equals(car.getType())) {
                    spinnerCarType.setSelection(i);
                    break;
                }
            }

            etCarPrice.setText(String.valueOf(car.getPricePerDay()));
            switchAvailability.setChecked(car.isAvailability());
        }

        AlertDialog dialog = builder.create();

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etCarName.getText().toString().trim();
            String brand = etCarBrand.getText().toString().trim();
            String type = spinnerCarType.getSelectedItem().toString();
            String priceStr = etCarPrice.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please enter car name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(brand)) {
                Toast.makeText(this, "Please enter car brand", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(priceStr)) {
                Toast.makeText(this, "Please enter price", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean availability = switchAvailability.isChecked();

            // Show progress
            progressBarDialog.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);

            if (isEditMode && currentCar != null) {
                viewModel.updateCar(currentCar.getId(), name, brand, type, price, availability, selectedImageUri);
            } else {
                viewModel.addCar(name, brand, type, price, availability, selectedImageUri);
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog(CarDTO car) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Car")
                .setMessage("Are you sure you want to delete " + car.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCar(car.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // You can show preview if needed
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}