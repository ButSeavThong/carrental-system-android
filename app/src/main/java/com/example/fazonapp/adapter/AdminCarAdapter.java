package com.example.fazonapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fazonapp.R;
import com.example.fazonapp.dto.CarDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminCarAdapter extends RecyclerView.Adapter<AdminCarAdapter.CarViewHolder> {

    private List<CarDTO> cars = new ArrayList<>();
    private OnCarActionListener listener;

    public interface OnCarActionListener {
        void onEditClick(CarDTO car);
        void onDeleteClick(CarDTO car);
    }

    public AdminCarAdapter(OnCarActionListener listener) {
        this.listener = listener;
    }

    public void setCars(List<CarDTO> cars) {
        this.cars = cars;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car_admin, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarDTO car = cars.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    class CarViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCarImage;
        private TextView tvCarName, tvCarBrand, tvCarType, tvCarPrice, tvAvailability;
        private ImageButton btnEdit, btnDelete;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvCarBrand = itemView.findViewById(R.id.tvCarBrand);
            tvCarType = itemView.findViewById(R.id.tvCarType);
            tvCarPrice = itemView.findViewById(R.id.tvCarPrice);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(CarDTO car) {
            tvCarName.setText(car.getName());
            tvCarBrand.setText(car.getBrand());
            tvCarType.setText(car.getType());
            tvCarPrice.setText(String.format(Locale.US, "$%.2f/day", car.getPricePerDay()));

            // Set availability
            if (car.isAvailability()) {
                tvAvailability.setText(R.string.available);
                tvAvailability.setBackgroundResource(R.drawable.bg_badge_available);
            } else {
                tvAvailability.setText(R.string.unavailable);
                tvAvailability.setBackgroundResource(R.drawable.bg_badge_unavailable);
            }

            // Load image
            if (car.getImageUrl() != null && !car.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(car.getImageUrl())
                        .placeholder(R.drawable.ic_car)
                        .error(R.drawable.ic_car)
                        .centerCrop()
                        .into(ivCarImage);
            } else {
                ivCarImage.setImageResource(R.drawable.ic_car);
            }

            // Button listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(car);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(car);
                }
            });
        }
    }
}
