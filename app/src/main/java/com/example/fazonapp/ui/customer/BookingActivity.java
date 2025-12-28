package com.example.fazonapp.ui.customer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.fazonapp.R;
import com.example.fazonapp.utils.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private ImageView ivCarImage;
    private TextView tvCarName, tvCarBrand, tvPricePerDay;
    private Button btnStartDate, btnEndDate, btnConfirmBooking;
    private TextView tvDuration, tvTotalPrice;
    private ProgressBar progressBar;

    private BookingViewModel viewModel;

    private String carId, carName, carBrand, carType, carImageUrl;
    private double pricePerDay;
    private long startDateMillis = 0;
    private long endDateMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        initViews();
        getIntentData();
        displayCarInfo();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        ivCarImage = findViewById(R.id.ivCarImage);
        tvCarName = findViewById(R.id.tvCarName);
        tvCarBrand = findViewById(R.id.tvCarBrand);
        tvPricePerDay = findViewById(R.id.tvPricePerDay);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        progressBar = findViewById(R.id.progressBar);
    }

    private void getIntentData() {
        carId = getIntent().getStringExtra("CAR_ID");
        carName = getIntent().getStringExtra("CAR_NAME");
        carBrand = getIntent().getStringExtra("CAR_BRAND");
        carType = getIntent().getStringExtra("CAR_TYPE");
        pricePerDay = getIntent().getDoubleExtra("CAR_PRICE", 0);
        carImageUrl = getIntent().getStringExtra("CAR_IMAGE");
    }

    private void displayCarInfo() {
        tvCarName.setText(carName);
        tvCarBrand.setText(carBrand + " • " + carType);
        tvPricePerDay.setText(String.format(Locale.US, "$%.2f/day", pricePerDay));

        if (carImageUrl != null && !carImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(carImageUrl)
                    .placeholder(R.drawable.ic_car)
                    .into(ivCarImage);
        }
    }

    private void setupObservers() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                btnConfirmBooking.setEnabled(false);
            } else {
                progressBar.setVisibility(View.GONE);
                btnConfirmBooking.setEnabled(true);
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        btnStartDate.setOnClickListener(v -> showStartDatePicker());
        btnEndDate.setOnClickListener(v -> showEndDatePicker());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    startDateMillis = calendar.getTimeInMillis();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    btnStartDate.setText(sdf.format(calendar.getTime()));

                    calculateTotal();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        if (startDateMillis == 0) {
            Toast.makeText(this, "Please select start date first", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDateMillis);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    endDateMillis = calendar.getTimeInMillis();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    btnEndDate.setText(sdf.format(calendar.getTime()));

                    calculateTotal();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to start date + 1 day
        datePickerDialog.getDatePicker().setMinDate(startDateMillis + (24 * 60 * 60 * 1000));
        datePickerDialog.show();
    }

    private void calculateTotal() {
        if (startDateMillis > 0 && endDateMillis > 0) {
            long days = DateUtils.getDaysBetween(startDateMillis, endDateMillis);
            double total = days * pricePerDay;

            tvDuration.setText(String.format(Locale.US, "%d days", days));
            tvTotalPrice.setText(String.format(Locale.US, "$%.2f", total));

            btnConfirmBooking.setEnabled(true);
        } else {
            btnConfirmBooking.setEnabled(false);
        }
    }

    private void confirmBooking() {
        if (startDateMillis == 0 || endDateMillis == 0) {
            Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.createBooking(carId, carName, startDateMillis, endDateMillis, pricePerDay);
    }
}