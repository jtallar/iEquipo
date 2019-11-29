package ar.edu.itba.inge.pab.ui.projects;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;
import ar.edu.itba.inge.pab.ui.ProjectAdapter;
import ar.edu.itba.inge.pab.ui.explore.ExploreViewModel;

public class ProjectsFragment extends Fragment {
    public static final String LOG_TAG = "ar.edu.itba.inge.pab.ui.projects.ProjectsFragment";
    public static final String className = "ProjectsFragment";
    private ProjectsViewModel projectsViewModel;

    private RecyclerView rvProjects;
    private GridLayoutManager gridLayoutManager;
    private ProjectAdapter adapter;

    private List<Project> data = new ArrayList<>();

    private DatabaseReference database;

    private String nextId;

    private CardView emptyCard;
    private ProgressBar loading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        projectsViewModel = ViewModelProviders.of(this).get(ProjectsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_projects, container, false);

        database = FirebaseDatabase.getInstance().getReference();

        ImageButton addButton = root.findViewById(R.id.add_project);
        Person loggedPerson = MainActivity.getLoggedPerson();
        if (loggedPerson.getClass() == Student.class)
            addButton.setVisibility(View.GONE);
        else {
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(v -> {
                ProjectsFragmentDirections.ActionNewProject action = ProjectsFragmentDirections.actionNewProject(nextId);
                Navigation.findNavController(root).navigate(action);
            });
        }

        rvProjects = root.findViewById(R.id.rv_projects);
        gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
        rvProjects.setLayoutManager(gridLayoutManager);
        adapter = new ProjectAdapter(data, project -> {
            ProjectsFragmentDirections.ActionSelectProject action = ProjectsFragmentDirections.actionSelectProject(project, className, project.getTitulo());
            Navigation.findNavController(root).navigate(action);
        });
        rvProjects.setAdapter(adapter);

        emptyCard = root.findViewById(R.id.empty_projects_card);
        TextView tvEmptyRoom = emptyCard.findViewById(R.id.card_no_element_text);
        tvEmptyRoom.setText(R.string.empty_projects_list);
        loading = root.findViewById(R.id.projects_loading_bar);

        getFeedList();

        return root;
    }

    private void getFeedList() {
        data.clear();
        projectsViewModel.getFeed().observe(this, projects -> {
            if (projects != null) {
                for (Project project : projects) {
                    if (MainActivity.getLoggedPerson().getActividades().contains(project.getId()) && !data.contains(project))
                        data.add(project);
                }
                adapter.notifyDataSetChanged();
                nextId = String.format("%s%s", getResources().getString(R.string.activity_prefix) , String.valueOf(projects.size()));
            }
            loading.setVisibility(View.GONE);
            if (!data.isEmpty())
                emptyCard.setVisibility(View.GONE);
            else
                emptyCard.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        projectsViewModel.cancelRequests();
    }
}
