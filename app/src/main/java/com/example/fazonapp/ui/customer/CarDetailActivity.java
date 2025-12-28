package com.example.fazonapp.ui.customer;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.fazonapp.R;
import com.example.fazonapp.dto.CarDTO;
import java.util.Locale;

public class CarDetailActivity extends AppCompatActivity {

    private ImageView ivCarImage;
    private TextView tvCarName, tvCarBrand, tvCarType, tvCarPrice, tvAvailability;
    private Button btnBook;

    private CarViewModel viewModel;
    private CarDTO currentCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        viewModel = new ViewModelProvider(this).get(CarViewModel.class);

        initViews();

        // Get car ID from intent
        String carId = getIntent().getStringExtra("CAR_ID");
        if (carId != null) {
            loadCarDetails(carId);
        } else {
            Toast.makeText(this, "Error loading car details", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupListeners();
    }

    private void initViews() {
        ivCarImage = findViewById(R.id.ivCarImage);
        tvCarName = findViewById(R.id.tvCarName);
        tvCarBrand = findViewById(R.id.tvCarBrand);
        tvCarType = findViewById(R.id.tvCarType);
        tvCarPrice = findViewById(R.id.tvCarPrice);
        tvAvailability = findViewById(R.id.tvAvailability);
        btnBook = findViewById(R.id.btnBook);
    }

    private void loadCarDetails(String carId) {
        viewModel.getCarById(carId, new CarViewModel.CarCallback() {
            @Override
            public void onSuccess(CarDTO car) {
                currentCar = car;
                displayCarDetails(car);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CarDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayCarDetails(CarDTO car) {
        tvCarName.setText(car.getName());
        tvCarBrand.setText(car.getBrand());
        tvCarType.setText(car.getType());
        tvCarPrice.setText(String.format(Locale.US, "$%.2f", car.getPricePerDay()));

        // Set availability
        if (car.isAvailability()) {
            tvAvailability.setText(R.string.available);
            tvAvailability.setBackgroundResource(R.drawable.bg_badge_available);
            btnBook.setEnabled(true);
        } else {
            tvAvailability.setText(R.string.unavailable);
            tvAvailability.setBackgroundResource(R.drawable.bg_badge_unavailable);
            btnBook.setEnabled(false);
        }

        // Load image
        if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(car.getImageUrl())
                    .placeholder(R.drawable.ic_car)
                    .error(R.drawable.ic_car)
                    .centerCrop()
                    .into(ivCarImage);
        }
    }

    private void setupListeners() {
        btnBook.setOnClickListener(v -> {
            if (currentCar != null) {
                Intent intent = new Intent(this, BookingActivity.class);
                intent.putExtra("CAR_ID", currentCar.getId());
                intent.putExtra("CAR_NAME", currentCar.getName());
                intent.putExtra("CAR_BRAND", currentCar.getBrand());
                intent.putExtra("CAR_TYPE", currentCar.getType());
                intent.putExtra("CAR_PRICE", currentCar.getPricePerDay());
                intent.putExtra("CAR_IMAGE", currentCar.getImageUrl());
                startActivity(intent);
            }
        });
    }
}
