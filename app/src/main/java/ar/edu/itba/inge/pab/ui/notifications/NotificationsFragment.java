package ar.edu.itba.inge.pab.ui.notifications;

import android.os.Bundle;
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
    private void goToProject(Project project) {
        NotificationsFragmentDirections.ActionSelectProject action = NotificationsFragmentDirections.actionSelectProject(project, className, project.getTitulo());
        Navigation.findNavController(root).navigate(action);
    }

    private void goToStudent(Student student, String projectId, String notifType) {
        NotificationsFragmentDirections.ActionSelectStudent action = NotificationsFragmentDirections.actionSelectStudent(student, projectId, notifType, student.getNombre());
        Navigation.findNavController(root).navigate(action);
    }

    private void getNotificationsList() {
        data.clear();
        notificationsViewModel.getNotifications().observe(this, notifications -> {
            if (notifications != null) {
                for (Notification notification : notifications) {
                    if (!data.contains(notification))
                        data.add(notification);
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
                goToStudent(student, notification.getProject(), notification.getType());
            }
        });
    }

    private void getProject(Notification notification) {
        notificationsViewModel.getProject(notification.getProject()).observe(this, project -> {
            if (project != null) {
                goToProject(project);
            }
        });
    }

    private void openMessageDialog(Notification notification) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_recieve_message, null);
        AlertDialog dialog = new AlertDialog.Builder(root.getContext()).setView(dialogView).create();

        TextView tvTitle = dialogView.findViewById(R.id.dialog_message_title);
        if (tvTitle != null) tvTitle.setText(notification.getTitle());
        TextView tvSubtitle = dialogView.findViewById(R.id.dialog_message_subtitle);
        if (tvSubtitle != null) tvSubtitle.setText(notification.getMessage());

        TextView tvMessage = dialogView.findViewById(R.id.dialog_message_content);
        if (tvMessage != null) {
            if (notification.getBody() != null)
                tvMessage.setText(notification.getBody());
            else
                tvMessage.setVisibility(View.GONE);
        }
        Button cancelButton = dialogView.findViewById(R.id.dialog_message_cancel);
        if (cancelButton != null) cancelButton.setOnClickListener(v -> dialog.dismiss());
        Button storeButton = dialogView.findViewById(R.id.dialog_message_ok);
        if (storeButton != null) storeButton.setOnClickListener(v -> {
            notificationsViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
            dialog.dismiss();
        });
        dialog.show();
    }
}