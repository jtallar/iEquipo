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

    private DatabaseReference database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_project, container, false);

        database = FirebaseDatabase.getInstance().getReference();

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
            Person loggedPerson = MainActivity.getLoggedPerson();
            Project newProject = new Project(projectId, loggedPerson.getId(), title.getText().toString(),
                    Integer.valueOf(credits.getText().toString()), description.getText().toString(),
                    schedule.getText().toString(), requirements.getText().toString(),
                    loggedPerson.getEmail(), Integer.valueOf(studentCant.getText().toString()));

            database.child("Feed").child(projectId).setValue(newProject);
            database.child("Usuarios").child("Profesores").child(loggedPerson.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Person teacher = dataSnapshot.getValue(Person.class);
                    if (teacher != null) {
                        teacher.addActivity(projectId);
                        database.child("Usuarios").child("Profesores").child(teacher.getId()).setValue(teacher);
                    }
                    Navigation.findNavController(root).navigateUp();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(ProjectsFragment.LOG_TAG, "error creando project");
                    Navigation.findNavController(root).navigateUp();
                }
            });
        };
    }
}
