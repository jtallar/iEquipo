package ar.edu.itba.inge.pab.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Student;


public class StudentFragment extends Fragment {
    private TextView name, career, id, hours;
    private Button action;
    private Student student;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student, container, false);

        if (getArguments() != null)
            student = StudentFragmentArgs.fromBundle(getArguments()).getStudent();

        name = root.findViewById(R.id.student_name);
        name.setText(student.getNombre());
        career = root.findViewById(R.id.student_career);
        career.setText(student.getCarrera());
        id = root.findViewById(R.id.student_id);
        id.setText(student.getId());
        hours = root.findViewById(R.id.student_hours);
        hours.setText(String.valueOf(student.getCreditos()));

        action = root.findViewById(R.id.student_btn_action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: DEFINE BUTTON ACION
                Navigation.findNavController(root).navigateUp();
            }
        });

        return root;
    }
}
