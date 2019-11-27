package ar.edu.itba.inge.pab.ui.students;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import ar.edu.itba.inge.pab.MyApplication;
import ar.edu.itba.inge.pab.R;
import ar.edu.itba.inge.pab.elements.Person;
import ar.edu.itba.inge.pab.elements.Project;
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;
import ar.edu.itba.inge.pab.ui.ProjectAdapter;
import ar.edu.itba.inge.pab.ui.explore.ExploreViewModel;

public class StudentsFragment extends Fragment {
    private static final String LOG_TAG = "ar.edu.itba.inge.pab.ui.students.StudentsFragment";
    private StudentsViewModel studentsViewModel;

    private RecyclerView rvStudents;
    private GridLayoutManager gridLayoutManager;
    private StudentAdapter adapter;

    private List<Student> data = new ArrayList<>();

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
        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_students, container, false);

        database = FirebaseDatabase.getInstance().getReference();

        Person loggedPerson = MainActivity.getLoggedPerson();

        rvStudents = root.findViewById(R.id.rv_students);
        gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
        rvStudents.setLayoutManager(gridLayoutManager);
        adapter = new StudentAdapter(data, new OnItemClickListener<Student>() {
            @Override
            public void onItemClick(Student element) {
                // TODO: GO TO STUDENT FRAGMENT
            }
        });
        rvStudents.setAdapter(adapter);

        emptyCard = root.findViewById(R.id.empty_students_card);
        TextView tvEmptyRoom = emptyCard.findViewById(R.id.card_no_element_text);
        tvEmptyRoom.setText(R.string.empty_students_list);
        loading = root.findViewById(R.id.students_loading_bar);

        database.child("Lista de Becarios Disponibles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Student student = snapshot.getValue(Student.class);
                    if (student != null && !data.contains(student)) {
                        data.add(student);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.appbar_explore_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != R.id.action_filter)
            return super.onOptionsItemSelected(item);

        // TODO: OPEN FILTER
        MyApplication.makeToast(getResources().getString(R.string.filter_button_message));

        return true;
    }
}
