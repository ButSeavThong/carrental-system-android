package com.example.fazonapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fazonapp.R;
import com.example.fazonapp.dto.UserDTO;
import com.example.fazonapp.utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserDTO> users = new ArrayList<>();
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserDTO user);
    }

    public UserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserDTO user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName, tvUserEmail, tvUserRole, tvUserNationality;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserNationality = itemView.findViewById(R.id.tvUserNationality);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUserClick(users.get(position));
                }
            });
        }

        public void bind(UserDTO user) {
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
            tvUserRole.setText(user.getRole());
            tvUserNationality.setText(user.getNationality());

            // Color code based on role
            if (Constants.ROLE_ADMIN.equals(user.getRole())) {
                tvUserRole.setBackgroundResource(R.drawable.bg_status_pending);
            } else {
                tvUserRole.setBackgroundResource(R.drawable.bg_status_approved);
            }
        }
    }
}