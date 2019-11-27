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
import androidx.lifecycle.ViewModelProviders;
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
    private static final String LOG_TAG = "ar.edu.itba.inge.pab.ui.projects.ProjectsFragment";
    private ProjectsViewModel projectsViewModel;

    private RecyclerView rvProjects;
    private GridLayoutManager gridLayoutManager;
    private ProjectAdapter adapter;

    private List<Project> data = new ArrayList<>();

    private DatabaseReference database;

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
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: GO TO NEW PROJECT FRAGMENT
                }
            });
        }

        rvProjects = root.findViewById(R.id.rv_projects);
        gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
        rvProjects.setLayoutManager(gridLayoutManager);
        adapter = new ProjectAdapter(data, new OnItemClickListener<Project>() {
            @Override
            public void onItemClick(Project element) {
                // TODO: GO TO PROJECT FRAGMENT
            }
        });
        rvProjects.setAdapter(adapter);

        emptyCard = root.findViewById(R.id.empty_projects_card);
        TextView tvEmptyRoom = emptyCard.findViewById(R.id.card_no_element_text);
        tvEmptyRoom.setText(R.string.empty_projects_list);
        loading = root.findViewById(R.id.projects_loading_bar);

        database.child("Feed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Project act = snapshot.getValue(Project.class);
                    if (act != null && loggedPerson.getActividades().contains(act.getId()) && !data.contains(act)) {
                        data.add(act);
                        adapter.notifyDataSetChanged();
                    }
                    // TODO: REVISAR ESTO CUANDO CAMBIEMOS EL FETCHING
                    loading.setVisibility(View.GONE);
                    if (!data.isEmpty())
                        emptyCard.setVisibility(View.GONE);
                    else
                        emptyCard.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(LOG_TAG, databaseError.getMessage());
            }
        });

        return root;
    }
}
