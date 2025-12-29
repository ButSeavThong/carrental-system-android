package com.example.fazonapp.ui.customer;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fazonapp.R;
import com.example.fazonapp.adapter.BookingAdapter;
import com.example.fazonapp.dto.BookingDTO;

public class MyBookingsActivity extends AppCompatActivity implements BookingAdapter.OnBookingClickListener {

    private Toolbar toolbar;
    private RecyclerView rvBookings;
    private LinearLayout emptyState;
    private ProgressBar progressBar;

    private BookingAdapter bookingAdapter;
    private BookingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        viewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupObservers();

        // Load bookings
        viewModel.loadMyBookings();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvBookings = findViewById(R.id.rvBookings);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Bookings");
        }
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(bookingAdapter);
    }

    private void setupObservers() {
        viewModel.getBookingsLiveData().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                bookingAdapter.setBookings(bookings);
                rvBookings.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                rvBookings.setVisibility(View.GONE);
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
    }

    @Override
    public void onBookingClick(BookingDTO booking) {
        // Can show booking details dialog
        Toast.makeText(this, "Booking: " + booking.getCarName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}