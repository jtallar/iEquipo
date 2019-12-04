package ar.edu.itba.inge.pab.ui.students;

import android.app.AlertDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
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
import ar.edu.itba.inge.pab.elements.Student;
import ar.edu.itba.inge.pab.ui.GridLayoutAutofitManager;
import ar.edu.itba.inge.pab.ui.OnItemClickListener;
import ar.edu.itba.inge.pab.ui.ProjectAdapter;
import ar.edu.itba.inge.pab.ui.explore.ExploreViewModel;

public class StudentsFragment extends Fragment {
    private static final String LOG_TAG = "ar.edu.itba.inge.pab.ui.students.StudentsFragment";
    public static final String className = "StudentsFragment";
    private StudentsViewModel studentsViewModel;

    private RecyclerView rvStudents;
    private StudentAdapter adapter;

    private List<Student> data = new ArrayList<>();

    private DatabaseReference database;

    private CardView emptyCard;
    private ProgressBar loading;
    private ImageButton filter, sort;
    private final int filter_dialog = R.layout.filter_dialog, order_dialog = R.layout.order_dialog;


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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 1, RecyclerView.VERTICAL, false);
//        GridLayoutManager gridLayoutManager = new GridLayoutAutofitManager(this.getContext(), (int) getResources().getDimension(R.dimen.card_width), LinearLayoutManager.VERTICAL, false);
        rvStudents.setLayoutManager(gridLayoutManager);
        adapter = new StudentAdapter(data, student -> {
            StudentsFragmentDirections.ActionSelectStudent action = StudentsFragmentDirections.actionSelectStudent(student, null, null, student.getNombre());
            Navigation.findNavController(root).navigate(action);
        });
        rvStudents.setAdapter(adapter);

        emptyCard = root.findViewById(R.id.empty_students_card);
        TextView tvEmptyRoom = emptyCard.findViewById(R.id.card_no_element_text);
        tvEmptyRoom.setText(R.string.empty_students_list);
        loading = root.findViewById(R.id.students_loading_bar);
        filter = root.findViewById(R.id.action_filter);
        sort = root.findViewById(R.id.action_sort);


        getStudentsList();

        return root;
    }

    private void getStudentsList() {
        data.clear();
        studentsViewModel.getStudents().observe(this, students -> {
            if (students != null) {
                for (Student student : students) {
                    if (!data.contains(student) && student.getCreditos() > 0)
                        data.add(student);
                }
                adapter.sort(getComparator(prevSort));
            }
            loading.setVisibility(View.GONE);
            if (!data.isEmpty())
                emptyCard.setVisibility(View.GONE);
            else
                emptyCard.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.appbar_explore_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() != R.id.action_filter && item.getItemId() != R.id.action_sort)
            return super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.action_filter)
            createDialog(filter_dialog);
        if(item.getItemId() == R.id.action_sort)
            createDialog(order_dialog);

        return true;
    }

    private boolean prevCareer = false, prevHoursCB = false, prevPercCB = false, prevInfo = false, prevElec = false, prevIndus = false;
    private int prevHours = 0, prevPerc = 0;
    private int prevSort = R.id.legajo_btn;

    private void createDialog(int dialog_view){
        LayoutInflater inflater = this.getLayoutInflater();
        View root = inflater.inflate(dialog_view, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext()).setView(root);
        final AlertDialog dialog = dialogBuilder.create();

        if(dialog_view == order_dialog) {
            root.findViewById(prevSort).setEnabled(false);
            root.findViewById(R.id.legajo_btn).setOnClickListener(view -> {
                adapter.sort(getComparator(R.id.legajo_btn));
                prevSort = R.id.legajo_btn;
                dialog.dismiss();
            });
            root.findViewById(R.id.nombre_btn).setOnClickListener(view -> {
                adapter.sort(getComparator(R.id.nombre_btn));
                prevSort = R.id.nombre_btn;
                dialog.dismiss();
            });
            root.findViewById(R.id.carrera_btn).setOnClickListener(view -> {
                adapter.sort(getComparator(R.id.carrera_btn));
                prevSort = R.id.carrera_btn;
                dialog.dismiss();
            });
            root.findViewById(R.id.horas_btn).setOnClickListener(view -> {
                adapter.sort(getComparator(R.id.horas_btn));
                prevSort = R.id.horas_btn;
                dialog.dismiss();
            });
            root.findViewById(R.id.porc_carrera_btn).setOnClickListener(view -> {
                adapter.sort(getComparator(R.id.porc_carrera_btn));
                prevSort = R.id.porc_carrera_btn;
                dialog.dismiss();
            });
        }else{
            adapter.resetData();
            EditText editPerc = root.findViewById(R.id.edit_porcentaje), editHours = root.findViewById(R.id.edit_horas);
            CheckBox boxPerc = root.findViewById(R.id.cb_porcentaje), boxHours = root.findViewById(R.id.cb_horas),
                    boxCareer = root.findViewById(R.id.cb_carrera), boxInfo = root.findViewById(R.id.cb_info),
                    boxIndus = root.findViewById(R.id.cb_indus), boxElec = root.findViewById(R.id.cb_elec);
            previousValues(boxPerc, editPerc, boxHours, editHours, boxCareer, boxInfo, boxIndus, boxElec);

            root.findViewById(R.id.accept_btn).setOnClickListener(view -> {
                dialog.dismiss();
                manageFilters(boxPerc, editPerc, boxHours, editHours, boxCareer, boxInfo, boxIndus, boxElec);
            });

        }

        dialog.show();
    }

    private Comparator<Student> getComparator(int sort) {
        switch (sort) {
            case R.id.nombre_btn:
                return (s1, s2) -> s1.getNombre().compareTo(s2.getNombre());
            case R.id.carrera_btn:
                return (s1, s2) -> s1.getCarrera().compareTo(s2.getCarrera());
            case R.id.horas_btn:
                return (s1, s2) -> s1.getCreditos() - s2.getCreditos();
            case R.id.porc_carrera_btn:
                return (s1, s2) -> s1.getPorcentaje()- s2.getPorcentaje();
            case R.id.legajo_btn:
            default:
                return (s1, s2) -> s1.getId().compareTo(s2.getId());
        }
    }

    private void previousValues(CheckBox boxPerc, EditText editPerc, CheckBox boxHours, EditText editHours, CheckBox boxCareer, CheckBox boxInfo, CheckBox boxIndus, CheckBox boxElec){
        boxHours.setChecked(prevHoursCB);
        boxPerc.setChecked(prevPercCB);
        boxCareer.setChecked(prevCareer);
        boxInfo.setChecked(prevInfo);
        boxElec.setChecked(prevElec);
        boxIndus.setChecked(prevIndus);
        editHours.setText(String.valueOf(prevHours));
        editPerc.setText(String.valueOf(prevPerc));
    }

    private void manageFilters(CheckBox boxPerc, EditText editPerc, CheckBox boxHours, EditText editHours, CheckBox boxCareer, CheckBox boxInfo, CheckBox boxIndus, CheckBox boxElec) {
        if(boxHours.isChecked()) {
            int hours = 0;
            if(editHours.getText() != null)
                hours = Integer.parseInt(editHours.getText().toString());
            adapter.filterHours(hours);
            prevHoursCB = true;
            prevHours = hours;
        } else prevHoursCB = false;
        if(boxPerc.isChecked()){
            int perc = 0;
            if(editPerc.getText() != null)
                perc =  Integer.parseInt(editPerc.getText().toString());
            adapter.filterPerc(perc);
            prevPercCB = true;
            prevPerc = perc;
        } else prevPercCB = false;
        if(boxCareer.isChecked()) {
            prevCareer = true;
            ArrayList<String> careers = new ArrayList<>();
            if(boxInfo.isChecked())
                careers.add("Informatica");
            if(boxIndus.isChecked())
                careers.add("Industrial");
            if(boxElec.isChecked())
                careers.add("Electronica");
            adapter.filterCareer(careers);
        }
        else prevCareer = false;

        prevInfo = boxInfo.isChecked();
        prevIndus = boxIndus.isChecked();
        prevElec = boxElec.isChecked();

        if(!boxCareer.isChecked() && !boxHours.isChecked() && !boxPerc.isChecked()) {
            adapter.resetData();
            adapter.notifyDataSetChanged();
        }
    }
  
    @Override
    public void onStop() {
        super.onStop();
        studentsViewModel.cancelRequests();
    }
}
