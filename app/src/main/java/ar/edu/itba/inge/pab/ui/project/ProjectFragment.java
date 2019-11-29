package ar.edu.itba.inge.pab.ui.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.notifications.MyFirebaseMessagingService;

public class ProjectFragment extends Fragment {
    private TextView title, credits, studentCant, description, schedule, requirements;
    private Button action;
    private Project project;
    private String callingFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_project, container, false);

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

        action = root.findViewById(R.id.act_btn_action);
//        action.setText("");
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: DEFINIR ACCIONES Y TEXTO DE BOTON SEGUN CALLING FRAGMENT

                // TODO lo que sigue es para probar nomas
                MyFirebaseMessagingService.sendMessage("Request for Approval", "It seems to be working", MainActivity.getLoggedPerson().getId());

                Navigation.findNavController(root).navigateUp();
            }
        });

        return root;
    }
}
