package com.example.fazonapp.ui.customer;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fazonapp.R;
import com.example.fazonapp.adapter.CarAdapter;
import com.example.fazonapp.dto.CarDTO;

import com.example.fazonapp.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements CarAdapter.OnCarClickListener {

    private Toolbar toolbar;
    private RecyclerView rvCars;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    private Button btnFilterAll, btnFilterSUV, btnFilterSedan, btnFilterTruck;

    private CarAdapter carAdapter;
    private CarViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewModel = new ViewModelProvider(this).get(CarViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupObservers();
        setupFilterButtons();
        setupBottomNavigation();

        // Load cars
        viewModel.loadAvailableCars();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvCars = findViewById(R.id.rvCars);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterSUV = findViewById(R.id.btnFilterSUV);
        btnFilterSedan = findViewById(R.id.btnFilterSedan);
        btnFilterTruck = findViewById(R.id.btnFilterTruck);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void setupRecyclerView() {
        carAdapter = new CarAdapter(this);
        rvCars.setLayoutManager(new LinearLayoutManager(this));
        rvCars.setAdapter(carAdapter);
    }

    private void setupObservers() {
        // Observe filtered cars
        viewModel.getFilteredCarsLiveData().observe(this, cars -> {
            if (cars != null && !cars.isEmpty()) {
                carAdapter.setCars(cars);
                rvCars.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                rvCars.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                rvCars.setVisibility(View.GONE);
                emptyState.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // Observe errors
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> {
            viewModel.filterCarsByType("All");
            highlightButton(btnFilterAll);
        });

        btnFilterSUV.setOnClickListener(v -> {
            viewModel.filterCarsByType("SUV");
            highlightButton(btnFilterSUV);
        });

        btnFilterSedan.setOnClickListener(v -> {
            viewModel.filterCarsByType("Sedan");
            highlightButton(btnFilterSedan);
        });

        btnFilterTruck.setOnClickListener(v -> {
            viewModel.filterCarsByType("Truck");
            highlightButton(btnFilterTruck);
        });

        // Default selection
        highlightButton(btnFilterAll);
    }

    private void highlightButton(Button selectedButton) {
        // Reset all buttons
        btnFilterAll.setBackgroundResource(R.drawable.bg_input_field);
        btnFilterSUV.setBackgroundResource(R.drawable.bg_input_field);
        btnFilterSedan.setBackgroundResource(R.drawable.bg_input_field);
        btnFilterTruck.setBackgroundResource(R.drawable.bg_input_field);

        // Highlight selected
        selectedButton.setBackgroundResource(R.drawable.bg_button_primary);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_bookings) {
                startActivity(new Intent(this, MyBookingsActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }

    @Override
    public void onCarClick(CarDTO car) {
        Intent intent = new Intent(this, CarDetailActivity.class);
        intent.putExtra("CAR_ID", car.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            viewModel.loadAvailableCars();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}