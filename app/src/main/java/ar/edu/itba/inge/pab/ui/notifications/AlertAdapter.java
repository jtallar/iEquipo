package ar.edu.itba.inge.pab.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Alert;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {
    private List<Alert> data;
    private OnItemClickListener<Alert> listener;

    public AlertAdapter(List<Alert> data, OnItemClickListener<Alert> itemClickListener) {
        this.data = data;
        this.listener = itemClickListener;
    }

    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlertViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class AlertViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificationType;
        TextView tvNotificationContent;

        public AlertViewHolder (View itemView) {
            super(itemView);
            tvNotificationType = itemView.findViewById(R.id.tv_notification_type);
            tvNotificationContent = itemView.findViewById(R.id.tv_notification_content);
        }

        public void bind(final Alert alert, final OnItemClickListener<Alert> listener) {
            tvNotificationType.setText(alert.getType());
            tvNotificationContent.setText(alert.getContent());
            itemView.setOnClickListener(view -> listener.onItemClick(alert));
        }
    }
}
