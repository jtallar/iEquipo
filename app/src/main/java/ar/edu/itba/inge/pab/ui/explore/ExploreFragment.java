package ar.edu.itba.inge.pab.ui.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ar.edu.itba.inge.pab.MainActivity;
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.ui.GridLayoutAutofitManager;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;
import ar.edu.itba.inge.pab.ui.ProjectAdapter;

public class ExploreFragment extends Fragment {
    private static final String LOG_TAG = "ar.edu.itba.inge.pab.ui.explore.ExploreFragment";
    public static final String className = "ExploreFragment";
    private ExploreViewModel exploreViewModel;

    private RecyclerView rvExplore;
    private ProjectAdapter adapter;

    private List<Project> data = new ArrayList<>();

    private DatabaseReference database;

    private CardView emptyCard;
    private ProgressBar loading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        exploreViewModel = ViewModelProviders.of(this).get(ExploreViewModel.class);
        View root = inflater.inflate(R.layout.fragment_explore, container, false);

        database = FirebaseDatabase.getInstance().getReference();

        Person loggedPerson = MainActivity.getLoggedPerson();

        rvExplore = root.findViewById(R.id.rv_explore);
        // GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutAutofitManager(this.getContext(), (int) getResources().getDimension(R.dimen.card_width), LinearLayoutManager.VERTICAL, false);
        rvExplore.setLayoutManager(gridLayoutManager);
        adapter = new ProjectAdapter(data, className, project -> {
            ExploreFragmentDirections.ActionSelectProject action = ExploreFragmentDirections.actionSelectProject(project, className, null, project.getTitulo());
            Navigation.findNavController(root).navigate(action);
        });
        rvExplore.setAdapter(adapter);

        emptyCard = root.findViewById(R.id.empty_explore_card);
        TextView tvEmptyRoom = emptyCard.findViewById(R.id.card_no_element_text);
        tvEmptyRoom.setText(R.string.empty_projects_list);
        loading = root.findViewById(R.id.explore_loading_bar);

        getFeedList();

        return root;
    }

    private void getFeedList() {
        data.clear();
        exploreViewModel.getFeed().observe(this, projects -> {
            if (projects != null) {
                for (Project project : projects) {
                    if (!MainActivity.getLoggedPerson().getActividades().contains(project.getId()) && !data.contains(project) && project.getAlumnos().size() < project.getCantidad())
                        data.add(project);
                }
                data.sort((p1, p2) -> {
                    int ret = p1.getTitulo().compareTo(p2.getTitulo());
                    if (ret == 0)
                        ret = p1.getCreditos() - p2.getCreditos();
                    return ret;
                });
                adapter.notifyDataSetChanged();
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
        exploreViewModel.cancelRequests();
    }
}
