package ar.edu.itba.inge.pab.ui.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.media.audiofx.NoiseSuppressor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;

public class NotificationsFragment extends Fragment {
    public static final String className = "NotificationsFragment";
    private NotificationsViewModel notificationsViewModel;

    private RecyclerView rvNotification;
    private GridLayoutManager gridLayoutManager;
    private NotificationsAdapter adapter;

    private View root;

    private ArrayList<Notification> data = new ArrayList<>();

    private DatabaseReference database;

    private CardView emptyCard;
    private ProgressBar loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        root = inflater.inflate(R.layout.fragment_notifications, container, false);

        database = FirebaseDatabase.getInstance().getReference();

        rvNotification = root.findViewById(R.id.rv_notifications);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutAutofitManager(this.getContext(), (int) getResources().getDimension(R.dimen.card_width), LinearLayoutManager.VERTICAL, false);
        rvNotification.setLayoutManager(gridLayoutManager);
        adapter = new NotificationsAdapter(data, notification -> {
            Notification.NotificationType type = Notification.NotificationType.getNotificationType(notification.getType());
            if (type == null) return;
            switch (type) {
                case DOWN:
                case JOIN:
                    getStudent(notification);
                    break;
                case REQUEST:
                    getProject(notification);
                    break;
                case INFO:
                    openMessageDialog(notification);
                    break;
            }
        });
        rvNotification.setAdapter(adapter);

        emptyCard = root.findViewById(R.id.empty_notifications_card);
        TextView tvEmptyRoom = emptyCard.findViewById(R.id.card_no_element_text);
        tvEmptyRoom.setText(R.string.empty_notifications_list);
        loading = root.findViewById(R.id.notifications_loading_bar);

        getNotificationsList();

        return root;
    }

    /* FUNCIONES PARA USAR ENTRE LAS POSIBLES ACCIONES */
    private void goToProject(Project project, Notification notification) {
        cancelNotification(notification);
        NotificationsFragmentDirections.ActionSelectProject action = NotificationsFragmentDirections.actionSelectProject(project, className, notification.getId(), project.getTitulo());
        Navigation.findNavController(root).navigate(action);
    }

    private void goToStudent(Student student, String projectId, Notification notification) {
        cancelNotification(notification);
        NotificationsFragmentDirections.ActionSelectStudent action = NotificationsFragmentDirections.actionSelectStudent(student, projectId, notification, student.getNombre());
        Navigation.findNavController(root).navigate(action);
    }

    private void getNotificationsList() {
        notificationsViewModel.getNotifications().observe(this, notifications -> {
            if (notifications != null) {
                data.clear();
                for (Notification notification: notifications) {
                    cancelNotification(notification);
                    if (data.contains(notification))
                        MyApplication.getInstance().getApiRepository().deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
                     else data.add(0, notification);
                }
                adapter.notifyDataSetChanged();
            }
            loading.setVisibility(View.GONE);
            if (!data.isEmpty())
                emptyCard.setVisibility(View.GONE);
            else
                emptyCard.setVisibility(View.VISIBLE);
        });
    }

    private void getStudent(Notification notification) {
        notificationsViewModel.getStudent(notification.getSender()).observe(this, student -> {
            if (student != null) {
                goToStudent(student, notification.getProject(), notification);
            } else {
                notificationsViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
                data.remove(notification);
                MyApplication.makeToast(getString(R.string.toast_student_gone));
            }
        });
    }

    private void getProject(Notification notification) {
        notificationsViewModel.getProject(notification.getProject()).observe(this, project -> {
            if (project != null) {
                goToProject(project, notification);
            } else {
                notificationsViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
                data.remove(notification);
                MyApplication.makeToast(getString(R.string.toast_activity_gone));
            }
        });
    }

    private void openMessageDialog(Notification notification) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_recieve_message, null);
        AlertDialog dialog = new AlertDialog.Builder(root.getContext()).setView(dialogView).create();
        cancelNotification(notification);

        TextView tvTitle = dialogView.findViewById(R.id.dialog_message_title);
        if (tvTitle != null) tvTitle.setText(notification.getTitle());

        String teacherName = "";
        String searched = getString(R.string.notification_info_message).split(" ")[0];
        for (String aux : notification.getMessage().split(" ")) {
            if (aux.equals(searched)) break;
            teacherName = teacherName.concat(aux).concat(" ");
        }

        TextView tvSubtitle = dialogView.findViewById(R.id.dialog_message_subtitle);
        TextView tvMessage = dialogView.findViewById(R.id.dialog_message_content);

        if (tvMessage != null && tvSubtitle != null) {
            tvMessage.setMovementMethod(new ScrollingMovementMethod());
            if (notification.getBody() != null) {
                tvSubtitle.setText(String.format("%s %s", getResources().getString(R.string.notification_info_from), teacherName));
                tvMessage.setText(notification.getBody());
            } else {
                tvSubtitle.setText(notification.getMessage());
                tvMessage.setVisibility(View.GONE);
            }
        }
        Button cancelButton = dialogView.findViewById(R.id.dialog_message_cancel);
        if (cancelButton != null) cancelButton.setOnClickListener(v -> dialog.dismiss());
        Button storeButton = dialogView.findViewById(R.id.dialog_message_ok);
        if (storeButton != null) storeButton.setOnClickListener(v -> {
            notificationsViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
            data.remove(notification);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        notificationsViewModel.cancelRequests();
    }

    private void cancelNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) MainActivity.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification.hashCode2());
    }
}