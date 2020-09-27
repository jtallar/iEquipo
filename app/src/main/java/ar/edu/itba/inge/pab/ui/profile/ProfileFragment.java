package ar.edu.itba.inge.pab.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.concurrent.atomic.AtomicReference;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;

public class ProfileFragment extends Fragment {
    private ProfileViewModel studentViewModel;
    private TextView activities;
    private Person loggedPerson;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_student, container, false);

        TextView name = root.findViewById(R.id.student_name);
        TextView career = root.findViewById(R.id.student_career);
        TextView id = root.findViewById(R.id.student_id);
        TextView hours = root.findViewById(R.id.student_hours);
        TextView percentage = root.findViewById(R.id.student_percentage);
        ProgressBar careerProgressBar = root.findViewById(R.id.career_progress_bar);
        TextView mail = root.findViewById(R.id.student_mail);
        activities = root.findViewById(R.id.student_activities);

        root.findViewById(R.id.student_button_panel).setVisibility(View.GONE);

        loggedPerson = MainActivity.getLoggedPerson();

        name.setText(loggedPerson.getNombre());
        mail.setText(loggedPerson.getEmail());
        if (loggedPerson.getClass() == Student.class) {
            Student student = (Student) loggedPerson;

            id.setText(loggedPerson.getId());
            career.setText(student.getCarrera());
            int credits = student.getCreditos();
            hours.setText(String.valueOf((credits >= 0) ? credits : 0));
            percentage.setText(String.format("%s%s",(String.valueOf(student.getPorcentaje())),"%"));
            careerProgressBar.setProgress((student.getPorcentaje()));
        } else {
            id.setText(loggedPerson.getId().substring(1)); // Remove P from ID
            career.setVisibility(View.GONE);
            root.findViewById(R.id.layout_student_hours).setVisibility(View.GONE);
            hours.setVisibility(View.GONE);
            root.findViewById(R.id.title_student_career_progress).setVisibility(View.GONE);
            percentage.setVisibility(View.GONE);
            careerProgressBar.setVisibility(View.GONE);
        }
        fillActivities();

        return root;
    }

    private void fillActivities() {
        AtomicReference<String> text = new AtomicReference<>();
        for (String str: loggedPerson.getActividades())
            studentViewModel.singleGetProject(str).observe(this, project -> {
                if (project == null) return;
                String aux = text.get();
                if (aux == null) aux = "";
                text.set(aux + project.getTitulo() + "\n");
                activities.setText(text.get());
            });
        if (loggedPerson.getActividades().size() == 0) activities.setText(MyApplication.getStringResource(R.string.activity_empty));
    }

    @Override
    public void onStop() {
        super.onStop();
        studentViewModel.cancelRequests();
    }
}
