package com.example.fazonapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<CarDTO> cars = new ArrayList<>();
    private OnCarClickListener listener;

    public interface OnCarClickListener {
        void onCarClick(CarDTO car);
    }

    public CarAdapter(OnCarClickListener listener) {
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
                .inflate(R.layout.item_car, parent, false);
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

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCarImage = itemView.findViewById(R.id.ivCarImage);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvCarBrand = itemView.findViewById(R.id.tvCarBrand);
            tvCarType = itemView.findViewById(R.id.tvCarType);
            tvCarPrice = itemView.findViewById(R.id.tvCarPrice);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCarClick(cars.get(position));
                }
            });
        }

        public void bind(CarDTO car) {
            tvCarName.setText(car.getName());
            tvCarBrand.setText(car.getBrand());
            tvCarType.setText(car.getType());
            tvCarPrice.setText(String.format(Locale.US, "$%.2f", car.getPricePerDay()));

            // Set availability
            if (car.isAvailability()) {
                tvAvailability.setText(R.string.available);
                tvAvailability.setBackgroundResource(R.drawable.bg_badge_available);
            } else {
                tvAvailability.setText(R.string.unavailable);
                tvAvailability.setBackgroundResource(R.drawable.bg_badge_unavailable);
            }

            // Load image using Glide
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
        }
    }
}