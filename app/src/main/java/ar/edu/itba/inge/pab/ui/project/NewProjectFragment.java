package ar.edu.itba.inge.pab.ui.project;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.ui.projects.ProjectsFragment;

public class NewProjectFragment extends Fragment {
    private EditText title, credits, studentCant, description, schedule, requirements;
    private Button cancel, publish;
    private String projectId;
    private Project existing;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_project, container, false);

        if (getArguments() != null) {
            existing = NewProjectFragmentArgs.fromBundle(getArguments()).getProject();
            projectId = existing.getId();
        }

        title = root.findViewById(R.id.new_act_title);
        credits = root.findViewById(R.id.new_act_credits);
        studentCant = root.findViewById(R.id.new_act_student_cant);
        description = root.findViewById(R.id.new_act_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        schedule = root.findViewById(R.id.new_act_schedule);
        schedule.setMovementMethod(new ScrollingMovementMethod());
        requirements = root.findViewById(R.id.new_act_requirements);
        requirements.setMovementMethod(new ScrollingMovementMethod());
        cancel = root.findViewById(R.id.new_act_btn_cancel);
        publish = root.findViewById(R.id.new_act_btn_publish);

        if (existing != null && existing.getIdDocente() != null) {
            title.setText(existing.getTitulo());
            credits.setText(String.valueOf(existing.getCreditos()));
            studentCant.setText(String.valueOf(existing.getCantidad()));
            description.setText(existing.getDescripcion());
            schedule.setText(existing.getHorarios());
            requirements.setText(existing.getRequisitos());
            publish.setText(getResources().getString(R.string.button_edit));
        }

        cancel.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());
        if (existing != null && existing.getIdDocente() != null)
            publish.setOnClickListener(getEditListener(root));
        else
            publish.setOnClickListener(getPublishListener(root));

        return root;
    }

    private View.OnClickListener getPublishListener(View root) {
        return v -> {
            if (credits.getText() == null || studentCant.getText() == null) return;

            Person teacher = MainActivity.getLoggedPerson();

            Project newProject = new Project(projectId, teacher.getId(), title.getText().toString(),
                    Integer.valueOf(credits.getText().toString()), description.getText().toString(),
                    schedule.getText().toString(), requirements.getText().toString(),
                    teacher.getEmail(), Integer.valueOf(studentCant.getText().toString()));

            MyApplication.getInstance().getApiRepository().setProject(newProject);
            teacher.addActivity(projectId);
            MainActivity.setLoggedPerson(teacher);
            MyApplication.getInstance().getApiRepository().setTeacher(teacher);

            Navigation.findNavController(root).navigateUp();
        };
    }

    private View.OnClickListener getEditListener(View root) {
        return v -> {
            if (credits.getText() == null || studentCant.getText() == null) return;

            existing.editProject(title.getText().toString(), Integer.valueOf(credits.getText().toString()), description.getText().toString(),
                    schedule.getText().toString(), requirements.getText().toString(), Integer.valueOf(studentCant.getText().toString()));

            MyApplication.getInstance().getApiRepository().setProject(existing);
            Navigation.findNavController(root).navigate(NewProjectFragmentDirections.actionFinishEditProject());
            MyApplication.makeToast(getResources().getString(R.string.toast_edited_project));
        };
    }
}
