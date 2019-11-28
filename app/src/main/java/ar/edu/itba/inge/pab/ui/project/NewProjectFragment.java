package ar.edu.itba.inge.pab.ui.project;

import android.os.Bundle;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_project, container, false);

        if (getArguments() != null)
            projectId = NewProjectFragmentArgs.fromBundle(getArguments()).getActivityId();

        title = root.findViewById(R.id.new_act_title);
        credits = root.findViewById(R.id.new_act_credits);
        studentCant = root.findViewById(R.id.new_act_student_cant);
        description = root.findViewById(R.id.new_act_description);
        schedule = root.findViewById(R.id.new_act_schedule);
        requirements = root.findViewById(R.id.new_act_requirements);
        cancel = root.findViewById(R.id.new_act_btn_cancel);
        publish = root.findViewById(R.id.new_act_btn_publish);

        cancel.setOnClickListener(v -> Navigation.findNavController(root).navigateUp());
        publish.setOnClickListener(getPublishListener(root));

        return root;
    }

    private View.OnClickListener getPublishListener(View root) {
        return v -> {
            if (credits.getText() == null || studentCant.getText() == null) return;

            Person loggedPerson = MainActivity.getLoggedPerson();

            Project newProject = new Project(projectId, loggedPerson.getId(), title.getText().toString(),
                    Integer.valueOf(credits.getText().toString()), description.getText().toString(),
                    schedule.getText().toString(), requirements.getText().toString(),
                    loggedPerson.getEmail(), Integer.valueOf(studentCant.getText().toString()));

            MyApplication.getInstance().getApiRepository().setProject(newProject);
            Person teacher = MainActivity.getLoggedPerson();
            teacher.addActivity(projectId);
            MainActivity.setLoggedPerson(teacher);
            MyApplication.getInstance().getApiRepository().setTeacher(teacher);

            Navigation.findNavController(root).navigateUp();
        };
    }
}
