package ar.edu.itba.inge.pab.ui.student;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;
import ar.edu.itba.inge.pab.ui.students.StudentsFragment;


public class StudentFragment extends Fragment {
    private StudentViewModel studentViewModel;
    private TextView name, career, id, hours;
    private Button actionRight, actionLeft;
    private Student student;
    private String projectID;
    private Notification notification;
    private View root;

    private Project selectedProject;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentViewModel = ViewModelProviders.of(this).get(StudentViewModel.class);
        root = inflater.inflate(R.layout.fragment_student, container, false);

        if (getArguments() != null) {
            StudentFragmentArgs args = StudentFragmentArgs.fromBundle(getArguments());
            student = args.getStudent();
            projectID = args.getProjectId();
            notification = args.getNotification();
            if (projectID != null)
                studentViewModel.setViewModelProject(projectID);
        }

        name = root.findViewById(R.id.student_name);
        name.setText(student.getNombre());
        career = root.findViewById(R.id.student_career);
        career.setText(student.getCarrera());
        id = root.findViewById(R.id.student_id);
        id.setText(student.getId());
        hours = root.findViewById(R.id.student_hours);
        hours.setText(String.valueOf(student.getCreditos()));

        actionLeft = root.findViewById(R.id.student_btn_left_action);
        actionRight = root.findViewById(R.id.student_btn_right_action);
        if (projectID == null) {
            // Called by StudentsFragment
            actionLeft.setVisibility(View.GONE);
            actionRight.setText(getResources().getString(R.string.button_add_student));
            actionRight.setOnClickListener(v -> getLoggedProjects());
        } else {
            // Called by NotificationsFragment
            studentViewModel.getProject().observe(this, project -> {
                if (project != null) {
                    selectedProject = project;
                    Notification.NotificationType type = Notification.NotificationType.getNotificationType(notification.getType());
                    if (type == null) return;
                    switch (type) {
                        case JOIN:
                            actionLeft.setText(String.format("%s %s", getResources().getString(R.string.button_reject_join), project.getTitulo()));
                            actionLeft.setOnClickListener(v -> rejectRequest(Notification.NotificationType.JOIN));
                            actionRight.setText(String.format("%s %s", getResources().getString(R.string.button_accept_join), project.getTitulo()));
                            actionRight.setOnClickListener(v -> acceptRequest());
                            break;
                        case DOWN:
                            actionLeft.setText(String.format("%s %s", getResources().getString(R.string.button_reject_out), project.getTitulo()));
                            actionLeft.setOnClickListener(v -> rejectRequest(Notification.NotificationType.DOWN));
                            actionRight.setText(String.format("%s %s", getResources().getString(R.string.button_accept_out), project.getTitulo()));
                            actionRight.setOnClickListener(v -> removeStudent());
                            break;
                        default:
                            actionLeft.setVisibility(View.GONE);
                            actionRight.setVisibility(View.GONE);
                    }
                }
            });
        }
        return root;
    }

    private void getLoggedProjects() {
        List<Project> myProjects = new ArrayList<>();
        List<String> actIds = MainActivity.getLoggedPerson().getActividades();
        studentViewModel.getFeed().observe(this, projects -> {
            if (projects != null) {
                for (Project project : projects) {
                    if (actIds.contains(project.getId()) && !myProjects.contains(project) && project.getAlumnos().size() < project.getCantidad())
                        myProjects.add(project);
                }
                projectsDialog(myProjects);
            }
        });
    }

    private void projectsDialog(List<Project> projects) {
        if (projects.size() == 0) {
            MyApplication.makeToast(getResources().getString(R.string.student_no_activities_message));
            return;
        }
        String[] projectNames = projects.stream().map(Project::getTitulo).toArray(String[]::new);
        selectedProject = projects.get(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.AlertDialogTheme);
        builder.setTitle(R.string.dialog_select_project_title)
                .setSingleChoiceItems(projectNames, 0, (dialog, which) -> selectedProject = projects.get(which))
                .setPositiveButton(R.string.button_request, (dialog, which) -> {
                    sendNotif(Notification.NotificationType.REQUEST,
                            String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_request_message), selectedProject.getTitulo()), selectedProject.getId(), student.getId());
                    Log.e(MainActivity.LOG_TAG, String.format("REQUESTED FOR %s", selectedProject.getTitulo()));
                    dialog.dismiss();
                    Navigation.findNavController(root).navigateUp();
                    MyApplication.makeToast(getResources().getString(R.string.toast_request_sent));
                })
                .setNegativeButton(R.string.button_cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // To be called when ACCEPT is pressed
    private void acceptRequest() {
        if (selectedProject != null) {
            selectedProject.addStudent(student.getId());
            studentViewModel.setProject(selectedProject);

            student.substractCreditos(selectedProject.getCreditos());
            student.addActivity(selectedProject.getId());
            studentViewModel.setStudent(student);
        }

        if (notification != null) studentViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
        sendNotif(Notification.NotificationType.INFO,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_info_accept_join), selectedProject.getTitulo()), selectedProject.getId(), student.getId());
        Navigation.findNavController(root).navigateUp();
    }

    private void rejectRequest(Notification.NotificationType type) {
        String message;
        if (type.equals(Notification.NotificationType.JOIN))
            message = getResources().getString(R.string.notification_info_reject_join);
        else // DOWN
            message = getResources().getString(R.string.notification_info_reject_down);

        if (notification != null) studentViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
        sendNotif(Notification.NotificationType.INFO,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), message, selectedProject.getTitulo()), selectedProject.getId(), student.getId());
        Navigation.findNavController(root).navigateUp();
    }

    private void removeStudent() {
        if (selectedProject != null) {
            selectedProject.removeStudent(student.getId());
            studentViewModel.setProject(selectedProject);

            student.addCreditos(selectedProject.getCreditos());
            student.removeActivity(selectedProject.getId());
            studentViewModel.setStudent(student);
        }

        if (notification != null) studentViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notification.getId());
        sendNotif(Notification.NotificationType.INFO,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_info_accept_down), selectedProject.getTitulo()), selectedProject.getId(), student.getId());
        Navigation.findNavController(root).navigateUp();
    }

    private void sendNotif(Notification.NotificationType type, String message, String projectId, String receiverId) {
        MyFirebaseMessagingService.sendMessage(new Notification(type.getTitle(), message, projectId, type), receiverId);
    }

    private void sendNotif(Notification.NotificationType type, String message, String body, String projectId, String receiverId) {
        MyFirebaseMessagingService.sendMessage(new Notification(type.getTitle(), message, body, projectId, type), receiverId);
    }

    @Override
    public void onStop() {
        super.onStop();
        studentViewModel.cancelRequests();
    }
}
