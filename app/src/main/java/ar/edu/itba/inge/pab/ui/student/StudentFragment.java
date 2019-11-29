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
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.students.StudentsFragment;


public class StudentFragment extends Fragment {
    private StudentViewModel studentViewModel;
    private TextView name, career, id, hours;
    private Button action;
    private Student student;
    private String projectID;
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

        action = root.findViewById(R.id.student_btn_action);
        if (projectID == null) {
            // Called by StudentsFragment
            action.setText(getResources().getString(R.string.button_add_student));
            action.setOnClickListener(v -> getLoggedProjects());
        } else {
            // Called by NotificationsFragment
            // TODO: VER COMO RECIBO EL TIPO DE NOTIFICACION, QUE HAGO SEGUN EL
            studentViewModel.getProject().observe(this, project -> {
                if (project != null) {
                    selectedProject = project;
//                    acceptRequest();
//                    rejectRequest();
//                    removeStudent();
                }
            });
            action.setVisibility(View.GONE);
        }
        return root;
    }

    private void projectsDialog(List<Project> projects) {
        if (projects.size() == 0) {
            MyApplication.makeToast(getResources().getString(R.string.student_no_activities_message));
            return;
        }
        String[] projectNames = projects.stream().map(Project::getTitulo).toArray(String[]::new);
        selectedProject = projects.get(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.dialog_select_project_title)
                .setSingleChoiceItems(projectNames, 0, (dialog, which) -> selectedProject = projects.get(which))
                .setPositiveButton(R.string.button_request, (dialog, which) -> {
                    // TODO: SEND NOTIFICATION
                    Log.e(MainActivity.LOG_TAG, String.format("REQUESTED FOR %s", selectedProject.getTitulo()));
                    dialog.dismiss();
                    Navigation.findNavController(root).navigateUp();
                })
                .setNegativeButton(R.string.button_cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void getLoggedProjects() {
        List<Project> myProjects = new ArrayList<>();
        List<String> actIds = MainActivity.getLoggedPerson().getActividades();
        studentViewModel.getFeed().observe(this, projects -> {
            if (projects != null) {
                for (Project project : projects) {
                    if (actIds.contains(project.getId()) && !myProjects.contains(project))
                        myProjects.add(project);
                }
                projectsDialog(myProjects);
            }
        });
    }

    // To be called when ACCEPT is pressed
    private void acceptRequest() {
        // TODO: NEED PROJECT TO ADD STUDENT TO IT

        if (selectedProject != null) {
            selectedProject.addStudent(student.getId());
            studentViewModel.setProject(selectedProject);

            student.addActivity(selectedProject.getId());
            studentViewModel.setStudent(student);
        }

        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    private void rejectRequest() {
        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    private void removeStudent() {
        if (selectedProject != null) {
            selectedProject.removeStudent(student.getId());
            studentViewModel.setProject(selectedProject);

            student.removeActivity(selectedProject.getId());
            studentViewModel.setStudent(student);
        }
        // TODO: SEND NOTIFICATION
        Navigation.findNavController(root).navigateUp();
    }

    @Override
    public void onStop() {
        super.onStop();
        studentViewModel.cancelRequests();
    }
}
