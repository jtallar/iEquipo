package ar.edu.itba.inge.pab.ui.student_profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import ar.edu.itba.inge.pab.LoginActivity;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Student;
public class StudentProfileFragment extends Fragment {
    public static final String  LOG_TAG = "ar.edu.itba.inge.pab.ui.projects.StudentProfileFragment";
    public static final String  className = "StudentProfileFragment";
    private static Person loggedPerson;

    private TextView name, career, id, hours, percentage;
    private ProgressBar             careerProgressBar;
    private StudentProfileViewModel studentProfileViewModel;
    private View                    root;
    private Intent                  intent;
    private Student student;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        studentProfileViewModel = ViewModelProviders.of(this).get(StudentProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_student_profile, container, false);


        intent =  getActivity().getIntent();
        loggedPerson = (Person) intent.getSerializableExtra(LoginActivity.EXTRA_PERSON);
        student = (Student) loggedPerson;

        name = root.findViewById(R.id.student_profile_name);
        career = root.findViewById(R.id.student_profile_career);
        id = root.findViewById(R.id.student_profile_id);
        hours = root.findViewById(R.id.student_profile_hours);
        percentage = root.findViewById(R.id.student_profile_percentage);
        careerProgressBar = root.findViewById(R.id.career_progress_bar);


        name.setText(student.getNombre());
        career.setText(student.getCarrera());
        id.setText(String.format("%s %s","Legajo:" ,student.getId()));
        hours.setText(String.format("%s %s","Horas Disponibles:" ,String.valueOf(student.getCreditos())));
        percentage.setText(String.format("%s%s",(String.valueOf(student.getPorcentaje())),"%"));
        careerProgressBar.setProgress((student.getPorcentaje()));


        return root;
    }

    @Override
    public void onStop() {
        super.onStop();
        studentProfileViewModel.cancelRequests();
    }
}
