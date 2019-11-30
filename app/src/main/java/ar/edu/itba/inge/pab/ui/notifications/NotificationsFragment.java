package ar.edu.itba.inge.pab.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
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
        gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
        rvNotification.setLayoutManager(gridLayoutManager);
        adapter = new NotificationsAdapter(data, new OnItemClickListener<Notification>() {
            @Override
            public void onItemClick(Notification element) {
                // TODO: OPEN NOTIF
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
    private OnItemClickListener goToProject(Project project) {
        return element -> {
            NotificationsFragmentDirections.ActionSelectProject action = NotificationsFragmentDirections.actionSelectProject(project, className, project.getTitulo());
            Navigation.findNavController(root).navigate(action);
        };
    }

    private OnItemClickListener goToStudent(Student student, String projectId) {
        return element -> {
            // TODO: AGREGAR ALGUN PARAMETRO MAS QUE INDIQUE QUE TIPO DE REQUEST ES
            NotificationsFragmentDirections.ActionSelectStudent action = NotificationsFragmentDirections.actionSelectStudent(student, projectId, student.getNombre());
            Navigation.findNavController(root).navigate(action);
        };
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
}