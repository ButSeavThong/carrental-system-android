package com.example.fazonapp.ui.admin;

import android.app.AlertDialog;
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
import com.example.fazonapp.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class ManageBookingActivity extends AppCompatActivity implements BookingAdapter.OnBookingClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvBookings;
    private LinearLayout emptyState;
    private ProgressBar progressBar;

    private BookingAdapter bookingAdapter;
    private AdminBookingViewModel viewModel;
    private List<BookingDTO> allBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_booking);

        viewModel = new ViewModelProvider(this).get(AdminBookingViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupTabLayout();
        setupObservers();

        // Load bookings
        viewModel.loadAllBookings();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tabLayout);
        rvBookings = findViewById(R.id.rvBookings);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Bookings");
        }
    }

    private void setupRecyclerView() {
        bookingAdapter = new BookingAdapter(this);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setAdapter(bookingAdapter);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filter = tab.getText().toString();
                viewModel.filterBookingsByStatus(filter, allBookings);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupObservers() {
        viewModel.getBookingsLiveData().observe(this, bookings -> {
            if (bookings != null) {
                allBookings = bookings;

                if (!bookings.isEmpty()) {
                    bookingAdapter.setBookings(bookings);
                    rvBookings.setVisibility(View.VISIBLE);
                    emptyState.setVisibility(View.GONE);
                } else {
                    rvBookings.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                }
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
    public void onBookingClick(BookingDTO booking) {
        showBookingActionDialog(booking);
    }

    private void showBookingActionDialog(BookingDTO booking) {
        String[] actions;

        if (Constants.STATUS_PENDING.equals(booking.getStatus())) {
            actions = new String[]{"Approve", "Reject", "Delete", "View Details"};
        } else {
            actions = new String[]{"View Details", "Delete"};
        }

        new AlertDialog.Builder(this)
                .setTitle("Booking Actions")
                .setItems(actions, (dialog, which) -> {
                    String selectedAction = actions[which];
                    handleBookingAction(selectedAction, booking);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleBookingAction(String action, BookingDTO booking) {
        switch (action) {
            case "Approve":
                viewModel.approveBooking(booking.getId(), booking.getCarId());
                break;
            case "Reject":
                viewModel.rejectBooking(booking.getId(), booking.getCarId());
                break;
            case "Delete":
                showDeleteBookingDialog(booking);
                break;
            case "View Details":
                showBookingDetails(booking);
                break;
        }
    }

    private void showDeleteBookingDialog(BookingDTO booking) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Booking")
                .setMessage("Are you sure you want to delete this booking?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteBooking(booking.getId(), booking.getCarId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showBookingDetails(BookingDTO booking) {
        String details = "Car: " + booking.getCarName() + "\n" +
                "Status: " + booking.getStatus() + "\n" +
                "Start Date: " + booking.getStartDate() + "\n" +
                "End Date: " + booking.getEndDate() + "\n" +
                "Duration: " + booking.getNumberOfDays() + " days\n" +
                "Total Price: $" + booking.getTotalPrice();

        new AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
