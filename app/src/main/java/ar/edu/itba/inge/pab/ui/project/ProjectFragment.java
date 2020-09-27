package ar.edu.itba.inge.pab.ui.project;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Notification;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;
import ar.edu.itba.inge.pab.ui.explore.ExploreFragment;
import ar.edu.itba.inge.pab.ui.notifications.NotificationsFragment;
import ar.edu.itba.inge.pab.ui.projects.ProjectsFragment;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;

public class ProjectFragment extends Fragment {
    private ProjectViewModel projectViewModel;
    private TextView title, credits, studentCant, description, schedule, requirements, professor;
    private RecyclerView rvStudents;
    private List<Student> students = new ArrayList<>();
    private StudentListAdapter adapter;

    private Button actionLeft, actionRight;
    private Project project;
    private String callingFragment;
    private String notificationId;

    private View root;

    private int deleteCount = 0;

    private enum ConfirmAction {DELETE_STUDENT, DELETE_PROJECT, REQUEST_OUT}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        projectViewModel = ViewModelProviders.of(this).get(ProjectViewModel.class);
        root = inflater.inflate(R.layout.fragment_project, container, false);


        if (getArguments() != null) {
            ProjectFragmentArgs args = ProjectFragmentArgs.fromBundle(getArguments());
            project = args.getProject();
            callingFragment = args.getCallingFragment();
            notificationId = args.getNotificationId();
        }

//        title = root.findViewById(R.id.act_title);
        credits = root.findViewById(R.id.act_credits);
        studentCant = root.findViewById(R.id.act_student_cant);
        description = root.findViewById(R.id.act_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        schedule = root.findViewById(R.id.act_schedule);
        schedule.setMovementMethod(new ScrollingMovementMethod());
        requirements = root.findViewById(R.id.act_requirements);
        requirements.setMovementMethod(new ScrollingMovementMethod());
        rvStudents = root.findViewById(R.id.rv_student_list);
        adapter = new StudentListAdapter(students, student -> openConfirmationDialog(ConfirmAction.DELETE_STUDENT, student));
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rvStudents.setAdapter(adapter);
        professor = root.findViewById(R.id.act_professor);
        projectViewModel.signleGetTeacher(project.getIdDocente()).observe(this, professor -> {
            if (professor == null) return;
            this.professor.setText(professor.getNombre());
        });

        refreshView();

        actionLeft = root.findViewById(R.id.act_btn_left_action);
        if (callingFragment == null) actionLeft.setVisibility(View.GONE);
        else {
            switch (callingFragment) {
                case ProjectsFragment.className:
                    if (MainActivity.getLoggedPerson().getClass() != Student.class && project.getAlumnos().size() > 0) {
                        actionLeft.setText(getResources().getString(R.string.project_action_btn_contact_students));
                        actionLeft.setOnClickListener(v -> openMessageDialog(MainActivity.getLoggedPerson()));
                    } else
                        actionLeft.setVisibility(View.GONE);
                    break;
                case NotificationsFragment.className:
                    actionLeft.setText(getResources().getString(R.string.project_action_btn_reject_request));
                    actionLeft.setOnClickListener(v -> rejectRequest());
                    break;
                case ExploreFragment.className:
                default:
                     actionLeft.setVisibility(View.GONE);
            }
        }

        actionRight = root.findViewById(R.id.act_btn_right_action);
        if (callingFragment == null) actionRight.setVisibility(View.GONE);
        else {
            switch (callingFragment) {
                case ProjectsFragment.className:
                    if (MainActivity.getLoggedPerson().getClass() == Student.class) {
                        actionRight.setText(getResources().getString(R.string.project_action_btn_request_out));
                        actionRight.setOnClickListener(v -> openConfirmationDialog(ConfirmAction.REQUEST_OUT, null));
                    } else {
                        actionRight.setText(getResources().getString(R.string.project_action_btn_delete));
                        actionRight.setOnClickListener(v -> openConfirmationDialog(ConfirmAction.DELETE_PROJECT, null));
                    }
                    break;
                case ExploreFragment.className:
                    actionRight.setText(getResources().getString(R.string.project_action_btn_request_in));
                    actionRight.setOnClickListener(v -> requestIn());
                    break;
                case NotificationsFragment.className:
                    actionRight.setText(getResources().getString(R.string.project_action_btn_accept_request));
                    actionRight.setOnClickListener(v -> acceptRequest());
                    break;
                default:
                    actionRight.setVisibility(View.GONE);
            }
        }

        return root;
    }

    private void openConfirmationDialog(ConfirmAction action, Student student) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_confirmation, null);
        AlertDialog dialog = new AlertDialog.Builder(root.getContext()).setView(dialogView).create();

        TextView confirmationText = dialogView.findViewById(R.id.dialog_confirmation_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_confirmation_cancel);
        if (cancelButton != null) cancelButton.setOnClickListener(v -> dialog.dismiss());
        Button runButton = dialogView.findViewById(R.id.dialog_confirmation_ok);

        if (runButton != null) {
            switch (action) {
                case DELETE_PROJECT:
                    confirmationText.setText(getResources().getString(R.string.dialog_message_delete_project));
                    runButton.setText(getResources().getString(R.string.dialog_delete));
                    runButton.setOnClickListener(v -> {
                        deleteProject();
                        dialog.dismiss();
                    });
                    break;
                case REQUEST_OUT:
                    confirmationText.setText(getResources().getString(R.string.dialog_message_delete_request_out));
                    runButton.setText(getResources().getString(R.string.project_action_btn_request_out));
                    runButton.setOnClickListener(v -> {
                        requestOut();
                        dialog.dismiss();
                    });
                    break;
                case DELETE_STUDENT:
                    confirmationText.setText(String.format("%s %s %s", getResources().getString(R.string.dialog_message_delete_student), student.getNombre(), getResources().getString(R.string.dialog_message_from_this_project)));
                    runButton.setText(getResources().getString(R.string.dialog_delete));
                    runButton.setOnClickListener(v -> {
                        removeStudent(student);
                        dialog.dismiss();
                    });
                    break;
            }
        }

        dialog.show();
    }

    private void openMessageDialog(Person loggedPerson) {
        String title = (loggedPerson.getClass() == Student.class) ? getString(R.string.dialog_message_title_student) : getString(R.string.dialog_message_title_teacher);

        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_send_message, null);
        AlertDialog dialog = new AlertDialog.Builder(root.getContext()).setView(dialogView).create();

        TextView tvTitle = dialogView.findViewById(R.id.dialog_message_title);
        if (tvTitle != null) tvTitle.setText(title);

        EditText input = dialogView.findViewById(R.id.dialog_message_content);
        Button cancelButton = dialogView.findViewById(R.id.dialog_message_cancel);
        if (cancelButton != null) cancelButton.setOnClickListener(v -> dialog.dismiss());
        Button sendButton = dialogView.findViewById(R.id.dialog_message_ok);
        if (sendButton != null) {
            if (loggedPerson.getClass() == Student.class) {
                sendButton.setOnClickListener(v -> {
                    if (input.getText() != null) {
                        sendNotif(Notification.NotificationType.INFO,
                                String.format("%s %s %s", loggedPerson.getNombre(), getResources().getString(R.string.notification_info_message), project.getTitulo()), input.getText().toString(), project.getId(), project.getIdDocente());
                        MyApplication.makeToast(getResources().getString(R.string.toast_message_sent));
                    }
                    dialog.dismiss();
                });
            } else {
                sendButton.setOnClickListener(v -> {
                    if (input.getText() != null) {
                        for (String studentId : project.getAlumnos())
                            sendNotif(Notification.NotificationType.INFO,
                                    String.format("%s %s %s", loggedPerson.getNombre(), getResources().getString(R.string.notification_info_message), project.getTitulo()), input.getText().toString(), project.getId(), studentId);
                        MyApplication.makeToast(getResources().getString(R.string.toast_message_sent));
                    }
                    dialog.dismiss();
                });
            }
        }
        dialog.show();
    }

    private void deleteProject() {
        Person teacher = MainActivity.getLoggedPerson();

        for (Student student : students) {
            student.addCreditos(project.getCreditos());
            student.removeActivity(project.getId());
            projectViewModel.setStudent(student);
            sendNotif(Notification.NotificationType.INFO,
                    String.format("%s %s %s", teacher.getNombre(), getResources().getString(R.string.notification_info_delete_project), project.getTitulo()), project.getId(), student.getId());
            increaseDeleteCount(root);
        }

        teacher.removeActivity(project.getId());
        MainActivity.setLoggedPerson(teacher);
        projectViewModel.setTeacher(teacher);

        projectViewModel.deleteProject(project.getId());

        increaseDeleteCount(root);
    }

    private void removeStudent(Student student) {
        student.addCreditos(project.getCreditos());
        student.removeActivity(project.getId());
        projectViewModel.setStudent(student);
        sendNotif(Notification.NotificationType.INFO,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_info_delete_student), project.getTitulo()), project.getId(), student.getId());

        project.removeStudent(student.getId());
        projectViewModel.setProject(project);
        studentCant.setText(String.format("%d/%d", project.getAlumnos().size(), project.getCantidad()));
        students.remove(student);
        adapter.notifyDataSetChanged();

        if (actionLeft.getVisibility() == View.VISIBLE && students.size() == 0)
            actionLeft.setVisibility(View.GONE);
    }

    private void requestIn() {
        sendNotif(Notification.NotificationType.JOIN,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_join_message), project.getTitulo()), project.getId(), project.getIdDocente());
        Navigation.findNavController(root).navigateUp();
        MyApplication.makeToast(getResources().getString(R.string.toast_request_sent));
    }

    private void requestOut() {
        sendNotif(Notification.NotificationType.DOWN,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_down_message), project.getTitulo()), project.getId(), project.getIdDocente());
        Navigation.findNavController(root).navigateUp();
        MyApplication.makeToast(getResources().getString(R.string.toast_request_sent));
    }

    private void acceptRequest() {
        if (project.getCantidad() == project.getAlumnos().size()) {
            MyApplication.makeToast(getResources().getString(R.string.error_project_full));
            Navigation.findNavController(root).navigateUp();
            return;
        }

        Student student = (Student) MainActivity.getLoggedPerson();
        project.addStudent(student.getId());
        projectViewModel.setProject(project);

        student.substractCreditos(project.getCreditos());
        student.addActivity(project.getId());
        MainActivity.setLoggedPerson(student);
        projectViewModel.setStudent(student);

        if (notificationId != null) projectViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notificationId);
        sendNotif(Notification.NotificationType.INFO,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_info_accept_request), project.getTitulo()), project.getId(), project.getIdDocente());
        Navigation.findNavController(root).navigateUp();
    }

    private void rejectRequest() {
        if (notificationId != null) projectViewModel.deleteNotification(MainActivity.getLoggedPerson().getId(), notificationId);
        sendNotif(Notification.NotificationType.INFO,
                String.format("%s %s %s", MainActivity.getLoggedPerson().getNombre(), getResources().getString(R.string.notification_info_reject_request), project.getTitulo()), project.getId(), project.getIdDocente());
        Navigation.findNavController(root).navigateUp();
    }

    private void increaseDeleteCount(View root) {
        deleteCount++;
        if (deleteCount >= project.getAlumnos().size() + 1) {
            Navigation.findNavController(root).navigateUp();
            MyApplication.makeToast(getResources().getString(R.string.toast_deleted_project));
        }
    }

    private void sendNotif(Notification.NotificationType type, String message, String projectId, String receiverId) {
        MyFirebaseMessagingService.sendMessage(new Notification(type.getTitle(), message, projectId, type), receiverId);
    }

    private void sendNotif(Notification.NotificationType type, String message, String body, String projectId, String receiverId) {
        Log.e(MainActivity.LOG_TAG, receiverId);
        MyFirebaseMessagingService.sendMessage(new Notification(type.getTitle(), message, body, projectId, type), receiverId);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // EDIT only posible if teacher opens it (in My Projects)
        if (MainActivity.getLoggedPerson().getClass() != Student.class)
            inflater.inflate(R.menu.appbar_project_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != R.id.action_project_settings)
            return super.onOptionsItemSelected(item);

        ProjectFragmentDirections.ActionEditProject action = ProjectFragmentDirections.actionEditProject(project, getResources().getString(R.string.title_edit_project));
        Navigation.findNavController(root).navigate(action);

        return true;
    }

    private void refreshView() {
//        title.setText(project.getTitulo());
//        getActivity().getActionBar().setTitle(project.getTitulo());
        credits.setText(String.valueOf(project.getCreditos()));
        studentCant.setText(String.format("%d/%d", project.getAlumnos().size(), project.getCantidad()));
        description.setText(project.getDescripcion());
        schedule.setText(project.getHorarios());
        requirements.setText(project.getRequisitos());

        students.clear();
        for (String studentId : project.getAlumnos()) {
            projectViewModel.getStudent(studentId).observe(this, student -> {
                if (student != null && !students.contains(student)) {
                    students.add(student);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        projectViewModel.cancelRequests();
    }
}
