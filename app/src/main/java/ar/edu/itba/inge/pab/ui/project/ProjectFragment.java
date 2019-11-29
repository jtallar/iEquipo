package ar.edu.itba.inge.pab.ui.project;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.explore.ExploreFragment;
import ar.edu.itba.inge.pab.ui.notifications.NotificationsFragment;
import ar.edu.itba.inge.pab.ui.projects.ProjectsFragment;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;

public class ProjectFragment extends Fragment {
    private ProjectViewModel projectViewModel;
    private TextView title, credits, studentCant, description, schedule, requirements;
    private Button actionLeft, actionRight;
    private Project project;
    private String callingFragment;

    private View root;

    private int deleteCount = 0;

    private enum ConfirmAction {DELETE, REQUEST_OUT, REQUEST_IN, ACCEPT}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        projectViewModel = ViewModelProviders.of(this).get(ProjectViewModel.class);
        root = inflater.inflate(R.layout.fragment_project, container, false);


        if (getArguments() != null) {
            ProjectFragmentArgs args = ProjectFragmentArgs.fromBundle(getArguments());
            project = args.getProject();
            callingFragment = args.getCallingFragment();
        }

        title = root.findViewById(R.id.act_title);
        title.setText(project.getTitulo());
        credits = root.findViewById(R.id.act_credits);
        credits.setText(String.valueOf(project.getCreditos()));
        studentCant = root.findViewById(R.id.act_student_cant);
        studentCant.setText(String.valueOf(project.getCantidad()));
        description = root.findViewById(R.id.act_description);
        description.setText(project.getDescripcion());
        schedule = root.findViewById(R.id.act_schedule);
        schedule.setText(project.getHorarios());
        requirements = root.findViewById(R.id.act_requirements);
        requirements.setText(project.getRequisitos());

        // TODO: DEFINIR ACCIONES Y TEXTO DE BOTON SEGUN CALLING FRAGMENT

        actionLeft = root.findViewById(R.id.act_btn_left_action);
        if (callingFragment == null) actionLeft.setVisibility(View.GONE);
        else {
            switch (callingFragment) {
                case ProjectsFragment.className:
                    if (MainActivity.getLoggedPerson().getClass() == Student.class)
                        actionLeft.setText(getResources().getString(R.string.project_action_btn_contact_teacher));
                    else
                        actionLeft.setText(getResources().getString(R.string.project_action_btn_contact_students));
                    actionLeft.setOnClickListener(v -> openMessageDialog(MainActivity.getLoggedPerson()));
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
                        actionRight.setOnClickListener(v -> openConfirmationDialog(ConfirmAction.REQUEST_OUT));
                    } else {
                        actionRight.setText(getResources().getString(R.string.project_action_btn_delete));
                        actionRight.setOnClickListener(v -> openConfirmationDialog(ConfirmAction.DELETE));
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
                    // TODO lo que sigue es para probar nomas
                    actionRight.setText("PROBAR NOTIF");
                    actionRight.setOnClickListener(v -> {
                        MyFirebaseMessagingService.sendMessage("Request de JT", "Holaa", "58639");
                        Navigation.findNavController(root).navigateUp();
                    });
//                     actionRight.setVisibility(View.GONE);
            }
        }

        return root;
    }

    private void openConfirmationDialog(ConfirmAction action) {
        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_confirmation, null);
        AlertDialog dialog = new AlertDialog.Builder(root.getContext()).setView(dialogView).create();

        TextView confirmationText = dialogView.findViewById(R.id.dialog_confirmation_message);
        Button cancelButton = dialogView.findViewById(R.id.dialog_confirmation_cancel);
        if (cancelButton != null) cancelButton.setOnClickListener(v -> dialog.dismiss());
        Button runButton = dialogView.findViewById(R.id.dialog_confirmation_ok);

        // TODO: VER SI ALGUIEN MAS ABRE EL CONFIRMATION DIALOG
        if (runButton != null) {
            switch (action) {
                case DELETE:
                    confirmationText.setText(getResources().getString(R.string.dialog_message_delete_project));
                    runButton.setOnClickListener(v -> {
                        deleteProject();
                        dialog.dismiss();
                    });
                    break;
                case REQUEST_OUT:
                    confirmationText.setText(getResources().getString(R.string.dialog_message_delete_request_out));
                    runButton.setOnClickListener(v -> {
                        requestOut();
                        dialog.dismiss();
                    });
                    break;
                default:
                    confirmationText.setText("NOTHING");
                    runButton.setOnClickListener(v -> dialog.dismiss());
            }
        }

        dialog.show();
    }

    private void openMessageDialog(Person loggedPerson) {
        String title = (loggedPerson.getClass() == Student.class) ? getString(R.string.dialog_message_title_student) : getString(R.string.dialog_message_title_teacher);

        View dialogView = this.getLayoutInflater().inflate(R.layout.dialog_message, null);
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
                    if (input.getText() != null) MyFirebaseMessagingService.sendMessage(String.format("New message from %s", loggedPerson.getNombre()), input.getText().toString(), project.getIdDocente());
                    dialog.dismiss();
                });
            } else {
                sendButton.setOnClickListener(v -> {
                    if (input.getText() != null) {
                        for (String studentId : project.getAlumnos())
                            MyFirebaseMessagingService.sendMessage(String.format("New message from %s", loggedPerson.getNombre()), input.getText().toString(), studentId);
                    }
                    dialog.dismiss();
                });
            }
        }
        dialog.show();
    }

    private void deleteProject() {
        for (String studentId : project.getAlumnos()) {
            projectViewModel.getStudent(studentId).observe(this, student -> {
                if (student != null) {
                    student.addCreditos(project.getCreditos());
                    student.removeActivity(project.getId());
                    projectViewModel.setStudent(student);
                }
                increaseDeleteCount(root);
            });
        }

        Person teacher = MainActivity.getLoggedPerson();
        teacher.removeActivity(project.getId());
        MainActivity.setLoggedPerson(teacher);
        projectViewModel.setTeacher(teacher);

        projectViewModel.deleteProject(project.getId());

        increaseDeleteCount(root);
    }

    private void requestIn() {
        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    private void requestOut() {
        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    private void acceptRequest() {
        if (project.getCantidad() == project.getAlumnos().size()) {
            MyApplication.makeToast(getResources().getString(R.string.error_project_full));
            return;
        }

        Student student = (Student) MainActivity.getLoggedPerson();
        project.addStudent(student.getId());
        projectViewModel.setProject(project);

        student.substractCreditos(project.getCreditos());
        student.addActivity(project.getId());
        MainActivity.setLoggedPerson(student);
        projectViewModel.setStudent(student);

        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    private void rejectRequest() {
        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    private void increaseDeleteCount(View root) {
        deleteCount++;
        if (deleteCount >= project.getAlumnos().size() + 1)
            Navigation.findNavController(root).navigateUp();
    }

    @Override
    public void onStop() {
        super.onStop();
        projectViewModel.cancelRequests();
    }
}
