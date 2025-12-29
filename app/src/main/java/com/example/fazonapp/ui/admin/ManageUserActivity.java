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
import com.example.fazonapp.adapter.UserAdapter;
import com.example.fazonapp.dto.UserDTO;
import com.example.fazonapp.utils.Constants;

public class ManageUserActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private Toolbar toolbar;
    private RecyclerView rvUsers;
    private LinearLayout emptyState;
    private ProgressBar progressBar;

    private UserAdapter userAdapter;
    private AdminUserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        viewModel = new ViewModelProvider(this).get(AdminUserViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupObservers();

        // Load users
        viewModel.loadAllUsers();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvUsers = findViewById(R.id.rvUsers);
        emptyState = findViewById(R.id.emptyState);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Users");
        }
    }

    private void setupRecyclerView() {
        userAdapter = new UserAdapter(this);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);
    }

    private void setupObservers() {
        viewModel.getUsersLiveData().observe(this, users -> {
            if (users != null && !users.isEmpty()) {
                userAdapter.setUsers(users);
                rvUsers.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                rvUsers.setVisibility(View.GONE);
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
    public void onUserClick(UserDTO user) {
        showUserActionDialog(user);
    }

    private void showUserActionDialog(UserDTO user) {
        String[] actions;

        if (Constants.ROLE_ADMIN.equals(user.getRole())) {
            actions = new String[]{"Demote to Customer", "Delete User", "View Details"};
        } else {
            actions = new String[]{"Promote to Admin", "Delete User", "View Details"};
        }

        new AlertDialog.Builder(this)
                .setTitle("User Actions")
                .setItems(actions, (dialog, which) -> {
                    String selectedAction = actions[which];
                    handleUserAction(selectedAction, user);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleUserAction(String action, UserDTO user) {
        switch (action) {
            case "Promote to Admin":
            case "Demote to Customer":
                viewModel.updateUserRole(user.getId(), user.getRole());
                break;
            case "Delete User":
                showDeleteUserDialog(user);
                break;
            case "View Details":
                showUserDetails(user);
                break;
        }
    }

    private void showDeleteUserDialog(UserDTO user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteUser(user.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showUserDetails(UserDTO user) {
        String details = "Name: " + user.getName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhone() + "\n" +
                "Nationality: " + user.getNationality() + "\n" +
                "Role: " + user.getRole();

        new AlertDialog.Builder(this)
                .setTitle("User Details")
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
