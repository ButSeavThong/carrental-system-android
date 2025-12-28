package com.example.fazonapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fazonapp.R;
import com.example.fazonapp.dto.BookingDTO;
import com.example.fazonapp.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingDTO> bookings = new ArrayList<>();
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(BookingDTO booking);
    }

    public BookingAdapter(OnBookingClickListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<BookingDTO> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingDTO booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCarName, tvStatus, tvStartDate, tvEndDate, tvDuration, tvTotalPrice;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvEndDate = itemView.findViewById(R.id.tvEndDate);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookingClick(bookings.get(position));
                }
            });
        }

        public void bind(BookingDTO booking) {
            tvCarName.setText(booking.getCarName());
            tvStartDate.setText(booking.getStartDate());
            tvEndDate.setText(booking.getEndDate());
            tvDuration.setText(String.format(Locale.US, "%d Days", booking.getNumberOfDays()));
            tvTotalPrice.setText(String.format(Locale.US, "$%.2f", booking.getTotalPrice()));

            // Set status with color
            tvStatus.setText(booking.getStatus());
            if (Constants.STATUS_PENDING.equals(booking.getStatus())) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            } else if (Constants.STATUS_APPROVED.equals(booking.getStatus())) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_approved);
            } else if (Constants.STATUS_REJECTED.equals(booking.getStatus())) {
                tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
            }
        }
    }
}