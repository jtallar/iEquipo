package ar.edu.itba.inge.pab.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.AlertViewHolder> {
    private List<Notification> data;
    private OnItemClickListener<Notification> listener;

    public NotificationsAdapter(List<Notification> data, OnItemClickListener<Notification> itemClickListener) {
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

        public void bind(final Notification notification, final OnItemClickListener<Notification> listener) {
            // TODO change getType here
            tvNotificationType.setText(notification.getType());
            tvNotificationContent.setText(notification.getMessage());
            itemView.setOnClickListener(view -> listener.onItemClick(notification));
        }
    }
}
